package cn.iocoder.yudao.module.bpm.service.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.model.simple.BpmSimpleModelNodeVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskStartModifyChildReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskReturnReqVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmFrozenTaskDO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmModifyRequestDO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmParentChildProcessLinkDO;
import cn.iocoder.yudao.module.bpm.dal.mysql.task.BpmModifyRequestMapper;
import cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants;
import cn.iocoder.yudao.module.bpm.enums.task.BpmFrozenTaskStatusEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmModifyChildProcessResumeStrategyEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmModifyRequestStatusEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmParentChildProcessLinkStatusEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.iocoder.yudao.module.bpm.service.definition.BpmProcessDefinitionService;
import cn.iocoder.yudao.module.bpm.service.task.dto.BpmModifyChildProcessStartResultDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants.*;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.*;

/**
 * 修改申请子流程 Service 实现类
 */
@Service
@Validated
@Slf4j
public class BpmModifyChildProcessServiceImpl implements BpmModifyChildProcessService {

    private static final String MODIFY_APPROVED_CONTINUE = "MODIFY_APPROVED_CONTINUE";
    private static final String MODIFY_APPROVED_RETURN = "MODIFY_APPROVED_RETURN";
    private static final String MODIFY_REJECTED_CONTINUE_MAIN = "MODIFY_REJECTED_CONTINUE_MAIN";

    @Resource
    private BpmTaskService taskService;
    @Resource
    @Lazy // 避免和 BpmProcessInstanceServiceImpl 相互注入时的循环依赖
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmParentChildProcessLinkService parentChildProcessLinkService;
    @Resource
    private BpmFrozenTaskService frozenTaskService;
    @Resource
    private BpmModifyRequestMapper modifyRequestMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BpmModifyChildProcessStartResultDTO startModifyChildProcess(Long operatorUserId, Long startUserId,
                                                                      BpmTaskStartModifyChildReqVO reqVO) {
        Task task = taskService.validateTask(operatorUserId, reqVO.getId());
        validateTaskCanStartModifyChild(task);
        ProcessInstance parentProcessInstance = validateParentProcessInstance(task);
        validateChildProcessDefinition(reqVO.getChildProcessDefinitionKey());

        BpmParentChildProcessLinkDO link = parentChildProcessLinkService.createLink(BpmParentChildProcessLinkDO.builder()
                .parentProcessInstanceId(task.getProcessInstanceId())
                .parentTaskId(task.getId())
                .parentTaskDefinitionKey(task.getTaskDefinitionKey())
                .childProcessDefinitionKey(reqVO.getChildProcessDefinitionKey())
                .lastActiveNodeKey(task.getTaskDefinitionKey())
                .resumeStrategy(reqVO.getResumeStrategy())
                .status(BpmParentChildProcessLinkStatusEnum.RUNNING.getStatus())
                .build());
        frozenTaskService.createFrozenTask(BpmFrozenTaskDO.builder()
                .processInstanceId(task.getProcessInstanceId())
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .assigneeUserId(NumberUtils.parseLong(task.getAssignee()))
                .freezeReason(reqVO.getReasonDetail())
                .linkId(link.getId())
                .status(BpmFrozenTaskStatusEnum.FROZEN.getStatus())
                .build());

        String childProcessInstanceId = processInstanceService.createProcessInstance(startUserId,
                buildChildProcessCreateReq(task, parentProcessInstance, reqVO, link.getId()));
        parentChildProcessLinkService.updateChildProcessInstanceId(link.getId(), childProcessInstanceId);

        log.info("[startModifyChildProcess][parentProcessInstanceId({}) parentTaskId({}) childProcessInstanceId({})]",
                task.getProcessInstanceId(), task.getId(), childProcessInstanceId);
        return new BpmModifyChildProcessStartResultDTO(link.getId(), childProcessInstanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleChildProcessCompleted(ProcessInstance childProcessInstance, Integer status, String reason) {
        BpmParentChildProcessLinkDO link = resolveLink(childProcessInstance);
        if (link == null) {
            return false;
        }

        ProcessInstance parentProcessInstance = processInstanceService.getProcessInstance(link.getParentProcessInstanceId());
        Task parentTask = taskService.getTask(link.getParentTaskId());
        String targetTaskDefinitionKey = resolveReturnTargetTaskDefinitionKey(childProcessInstance, link, parentTask);
        boolean rejected = ObjectUtil.equal(status, BpmProcessInstanceStatusEnum.REJECT.getStatus());
        boolean needReturn = !rejected
                && ObjectUtil.equal(link.getResumeStrategy(), BpmModifyChildProcessResumeStrategyEnum.RETURN_TO_TARGET_NODE.getType())
                && parentTask != null
                && StrUtil.isNotBlank(targetTaskDefinitionKey)
                && !StrUtil.equals(targetTaskDefinitionKey, parentTask.getTaskDefinitionKey());

        if (!rejected) {
            syncChildVariablesToParent(childProcessInstance, parentProcessInstance, link);
        }
        frozenTaskService.updateFrozenTaskStatusByLinkId(link.getId(), BpmFrozenTaskStatusEnum.RESUMED.getStatus());
        if (needReturn) {
            taskService.returnTask(NumberUtils.parseLong(parentTask.getAssignee()), new BpmTaskReturnReqVO()
                    .setId(parentTask.getId())
                    .setTargetTaskDefinitionKey(targetTaskDefinitionKey)
                    .setReason(StrUtil.blankToDefault(reason, "修改申请子流程完成，主流程回退重走")));
        }

        String resultType = rejected ? MODIFY_REJECTED_CONTINUE_MAIN : (needReturn ? MODIFY_APPROVED_RETURN : MODIFY_APPROVED_CONTINUE);
        parentChildProcessLinkService.updateLinkResult(link.getId(), BpmParentChildProcessLinkStatusEnum.RESUMED.getStatus(),
                buildResultJson(childProcessInstance, parentProcessInstance, status, reason, resultType, targetTaskDefinitionKey));
        updateModifyRequestCompleted(childProcessInstance.getId());
        log.info("[handleChildProcessCompleted][childProcessInstanceId({}) parentProcessInstanceId({}) resultType({}) targetTaskDefinitionKey({})]",
                childProcessInstance.getId(), link.getParentProcessInstanceId(), resultType, targetTaskDefinitionKey);
        return true;
    }

    private void updateModifyRequestCompleted(String childProcessInstanceId) {
        BpmModifyRequestDO modifyRequest = modifyRequestMapper.selectByChildProcessInstanceId(childProcessInstanceId);
        if (modifyRequest == null) {
            return;
        }
        BpmModifyRequestDO updateObj = new BpmModifyRequestDO();
        updateObj.setId(modifyRequest.getId());
        updateObj.setStatus(BpmModifyRequestStatusEnum.COMPLETED.getStatus());
        modifyRequestMapper.updateById(updateObj);
    }

    private void syncChildVariablesToParent(ProcessInstance childProcessInstance, ProcessInstance parentProcessInstance,
                                            BpmParentChildProcessLinkDO link) {
        if (parentProcessInstance == null || CollUtil.isEmpty(childProcessInstance.getProcessVariables())) {
            return;
        }
        List<BpmSimpleModelNodeVO.ModifyVariableMapping> variableMappings =
                resolveVariableMappings(parentProcessInstance, link);
        if (CollUtil.isEmpty(variableMappings)) {
            return;
        }

        Map<String, Object> childVariables = childProcessInstance.getProcessVariables();
        Map<String, Object> parentVariables = new HashMap<>();
        for (BpmSimpleModelNodeVO.ModifyVariableMapping mapping : variableMappings) {
            if (mapping == null || StrUtil.isBlank(mapping.getChildVariable())
                    || StrUtil.isBlank(mapping.getParentVariable())
                    || !childVariables.containsKey(mapping.getChildVariable())) {
                continue;
            }
            parentVariables.put(mapping.getParentVariable(), childVariables.get(mapping.getChildVariable()));
        }
        if (CollUtil.isEmpty(parentVariables)) {
            return;
        }
        processInstanceService.updateProcessInstanceVariables(parentProcessInstance.getId(), parentVariables);
        log.info("[syncChildVariablesToParent][childProcessInstanceId({}) parentProcessInstanceId({}) variables({})]",
                childProcessInstance.getId(), parentProcessInstance.getId(), parentVariables.keySet());
    }

    private List<BpmSimpleModelNodeVO.ModifyVariableMapping> resolveVariableMappings(ProcessInstance parentProcessInstance,
                                                                                     BpmParentChildProcessLinkDO link) {
        BpmSimpleModelNodeVO simpleModel = parseSimpleModel(parentProcessInstance);
        if (simpleModel == null) {
            return null;
        }
        BpmSimpleModelNodeVO.ModifyRequestSetting processSetting = simpleModel.getModifyRequestSetting();
        if (Boolean.TRUE.equals(processSetting == null ? null : processSetting.getEnable())) {
            return processSetting.getVariableMappings();
        }

        return null;
    }

    private BpmSimpleModelNodeVO parseSimpleModel(ProcessInstance parentProcessInstance) {
        BpmProcessDefinitionInfoDO processDefinitionInfo = processDefinitionService
                .getProcessDefinitionInfo(parentProcessInstance.getProcessDefinitionId());
        if (processDefinitionInfo == null || StrUtil.isBlank(processDefinitionInfo.getSimpleModel())) {
            return null;
        }
        return JsonUtils.parseObject(processDefinitionInfo.getSimpleModel(), BpmSimpleModelNodeVO.class);
    }

    private void validateTaskCanStartModifyChild(Task task) {
        if (task.isSuspended()) {
            throw exception(TASK_IS_PENDING);
        }
        if (Boolean.TRUE.equals(frozenTaskService.existsActiveFrozenTask(task.getProcessInstanceId(),
                BpmFrozenTaskStatusEnum.FROZEN.getStatus()))) {
            throw exception(TASK_OPERATE_FAIL_PROCESS_FROZEN);
        }
        // 节点级“允许发起修改”配置会在后续设计器能力补齐前接入，
        // 当前阶段先以显式传入的 childProcessDefinitionKey 为准进行运行时校验。
    }

    private ProcessInstance validateParentProcessInstance(Task task) {
        ProcessInstance parentProcessInstance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
        if (parentProcessInstance == null) {
            throw exception(PROCESS_INSTANCE_NOT_EXISTS);
        }
        return parentProcessInstance;
    }

    private void validateChildProcessDefinition(String childProcessDefinitionKey) {
        ProcessDefinition childProcessDefinition = processDefinitionService.getActiveProcessDefinition(childProcessDefinitionKey);
        if (childProcessDefinition == null) {
            throw exception(PROCESS_DEFINITION_NOT_EXISTS);
        }
    }

    private BpmProcessInstanceCreateReqDTO buildChildProcessCreateReq(Task task, ProcessInstance parentProcessInstance,
                                                                      BpmTaskStartModifyChildReqVO reqVO, Long linkId) {
        BpmProcessInstanceCreateReqDTO createReqDTO = new BpmProcessInstanceCreateReqDTO();
        createReqDTO.setProcessDefinitionKey(reqVO.getChildProcessDefinitionKey());
        createReqDTO.setBusinessKey(buildChildProcessBusinessKey(task));
        createReqDTO.setVariables(buildChildProcessVariables(task, parentProcessInstance, reqVO, linkId));
        return createReqDTO;
    }

    private Map<String, Object> buildChildProcessVariables(Task task, ProcessInstance parentProcessInstance,
                                                           BpmTaskStartModifyChildReqVO reqVO, Long linkId) {
        Map<String, Object> variables = new HashMap<>();
        inheritParentBusinessVariables(parentProcessInstance, variables);
        if (CollUtil.isNotEmpty(reqVO.getModifyPayload())) {
            variables.putAll(reqVO.getModifyPayload());
        }

        variables.put(PARENT_PROCESS_INSTANCE_ID, task.getProcessInstanceId());
        variables.put(PARENT_TASK_ID, task.getId());
        variables.put(PARENT_TASK_DEFINITION_KEY, task.getTaskDefinitionKey());
        variables.put(MODIFY_LINK_ID, linkId);
        variables.put(LAST_ACTIVE_NODE, task.getTaskDefinitionKey());
        variables.put(RETURN_NODE, task.getTaskDefinitionKey());
        variables.put(MODIFY_RESUME_STRATEGY, reqVO.getResumeStrategy());
        variables.put(MODIFY_REASON_DETAIL, reqVO.getReasonDetail());
        variables.put(BpmProcessVariableConstants.CAUSE, reqVO.getReasonDetail());
        if (reqVO.getReasonType() != null) {
            variables.put(MODIFY_TYPE, reqVO.getReasonType());
        }
        return variables;
    }

    private void inheritParentBusinessVariables(ProcessInstance parentProcessInstance, Map<String, Object> variables) {
        if (parentProcessInstance == null || CollUtil.isEmpty(parentProcessInstance.getProcessVariables())) {
            return;
        }
        Map<String, Object> parentVariables = parentProcessInstance.getProcessVariables();
        putIfPresent(variables, parentVariables, BILL_CODE);
        putIfPresent(variables, parentVariables, DEPT_ID);
        putIfPresent(variables, parentVariables, DEPT_NAME);
        putIfPresent(variables, parentVariables, COMPANY_ID);
        putIfPresent(variables, parentVariables, COMPANY_NAME);
    }

    private void putIfPresent(Map<String, Object> target, Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value != null) {
            target.put(key, value);
        }
    }

    private String buildChildProcessBusinessKey(Task task) {
        return StrUtil.format("modify-child:{}:{}:{}", task.getProcessInstanceId(), task.getId(), IdUtil.fastSimpleUUID());
    }

    private BpmParentChildProcessLinkDO resolveLink(ProcessInstance childProcessInstance) {
        BpmParentChildProcessLinkDO link = parentChildProcessLinkService.getLinkByChildProcessInstanceId(childProcessInstance.getId());
        if (link != null) {
            return link;
        }
        Long linkId = Convert.toLong(childProcessInstance.getProcessVariables().get(MODIFY_LINK_ID));
        if (linkId == null) {
            return null;
        }
        link = parentChildProcessLinkService.getLink(linkId);
        if (link != null && StrUtil.isBlank(link.getChildProcessInstanceId())) {
            parentChildProcessLinkService.updateChildProcessInstanceId(linkId, childProcessInstance.getId());
            link.setChildProcessInstanceId(childProcessInstance.getId());
        }
        return link;
    }

    private String resolveReturnTargetTaskDefinitionKey(ProcessInstance childProcessInstance, BpmParentChildProcessLinkDO link,
                                                        Task parentTask) {
        String targetTaskDefinitionKey = Convert.toStr(childProcessInstance.getProcessVariables().get(RETURN_NODE));
        if (StrUtil.isBlank(targetTaskDefinitionKey)) {
            targetTaskDefinitionKey = StrUtil.blankToDefault(link.getLastActiveNodeKey(), link.getParentTaskDefinitionKey());
        }
        if (parentTask == null) {
            return targetTaskDefinitionKey;
        }
        return StrUtil.blankToDefault(targetTaskDefinitionKey, parentTask.getTaskDefinitionKey());
    }

    private String buildResultJson(ProcessInstance childProcessInstance, ProcessInstance parentProcessInstance,
                                   Integer status, String reason, String resultType, String targetTaskDefinitionKey) {
        Map<String, Object> result = new HashMap<>();
        result.put("childProcessInstanceId", childProcessInstance.getId());
        result.put("parentProcessInstanceId", parentProcessInstance != null ? parentProcessInstance.getId() : null);
        result.put("childProcessStatus", status);
        result.put("childProcessReason", reason);
        result.put("resultType", resultType);
        result.put("targetTaskDefinitionKey", targetTaskDefinitionKey);
        return JsonUtils.toJsonString(result);
    }

}

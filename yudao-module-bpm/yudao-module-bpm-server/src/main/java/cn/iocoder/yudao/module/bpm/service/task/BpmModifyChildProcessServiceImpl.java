package cn.iocoder.yudao.module.bpm.service.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskStartModifyChildReqVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmFrozenTaskDO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmParentChildProcessLinkDO;
import cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants;
import cn.iocoder.yudao.module.bpm.enums.task.BpmFrozenTaskStatusEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmParentChildProcessLinkStatusEnum;
import cn.iocoder.yudao.module.bpm.service.definition.BpmProcessDefinitionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
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

    @Resource
    private BpmTaskService taskService;
    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmParentChildProcessLinkService parentChildProcessLinkService;
    @Resource
    private BpmFrozenTaskService frozenTaskService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startModifyChildProcess(Long userId, BpmTaskStartModifyChildReqVO reqVO) {
        Task task = taskService.validateTask(userId, reqVO.getId());
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

        String childProcessInstanceId = processInstanceService.createProcessInstance(userId,
                buildChildProcessCreateReq(task, parentProcessInstance, reqVO, link.getId()));
        parentChildProcessLinkService.updateChildProcessInstanceId(link.getId(), childProcessInstanceId);

        log.info("[startModifyChildProcess][parentProcessInstanceId({}) parentTaskId({}) childProcessInstanceId({})]",
                task.getProcessInstanceId(), task.getId(), childProcessInstanceId);
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

}

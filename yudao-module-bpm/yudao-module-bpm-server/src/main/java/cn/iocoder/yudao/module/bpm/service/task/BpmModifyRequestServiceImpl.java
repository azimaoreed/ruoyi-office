package cn.iocoder.yudao.module.bpm.service.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.model.simple.BpmSimpleModelNodeVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify.BpmModifyRequestCreateReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify.BpmModifyRequestHandleReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskStartModifyChildReqVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmModifyRequestDO;
import cn.iocoder.yudao.module.bpm.dal.mysql.task.BpmModifyRequestMapper;
import cn.iocoder.yudao.module.bpm.enums.task.BpmModifyChildProcessResumeStrategyEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmModifyRequestStatusEnum;
import cn.iocoder.yudao.module.bpm.framework.flowable.core.candidate.BpmTaskCandidateInvoker;
import cn.iocoder.yudao.module.bpm.service.definition.BpmProcessDefinitionService;
import cn.iocoder.yudao.module.bpm.service.task.dto.BpmModifyChildProcessStartResultDTO;
import jakarta.annotation.Resource;
import lombok.Data;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants.MODIFY_ACCEPT_USER_ID;
import static cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants.MODIFY_APPLICANT_USER_ID;
import static cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants.MODIFY_REQUEST_ID;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.*;

/**
 * 流程修改申请 Service 实现类。
 */
@Service
@Validated
public class BpmModifyRequestServiceImpl implements BpmModifyRequestService {

    private static final int NODE_SCOPE_ALL = 1;
    private static final int NODE_SCOPE_INCLUDE = 2;
    private static final int NODE_SCOPE_EXCLUDE = 3;

    private static final List<Integer> ACTIVE_STATUSES = Arrays.asList(
            BpmModifyRequestStatusEnum.PENDING_ACCEPT.getStatus(),
            BpmModifyRequestStatusEnum.MODIFYING.getStatus());

    @Resource
    private BpmModifyRequestMapper modifyRequestMapper;
    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmModifyChildProcessService modifyChildProcessService;
    @Resource
    private BpmTaskService bpmTaskService;
    @Resource
    private TaskService taskService;
    @Resource
    private BpmTaskCandidateInvoker taskCandidateInvoker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createModifyRequest(Long userId, BpmModifyRequestCreateReqVO reqVO) {
        ProcessInstance processInstance = validateProcessInstance(reqVO.getProcessInstanceId());
        Task task = resolveActiveTask(reqVO.getProcessInstanceId(), reqVO.getTaskId());
        BpmProcessDefinitionInfoDO processDefinitionInfo = processDefinitionService
                .getProcessDefinitionInfo(processInstance.getProcessDefinitionId());
        ResolvedModifyRequestSetting setting = resolveModifyRequestSetting(processDefinitionInfo, task.getTaskDefinitionKey(),
                reqVO.getChildProcessDefinitionKey(), reqVO.getResumeStrategy());

        validateApplicant(setting, userId);
        validateNodeScope(setting, task.getTaskDefinitionKey());
        validateReasonType(setting, reqVO.getReasonType());
        validateNoActiveModifyRequest(task.getProcessInstanceId());

        BpmModifyRequestDO modifyRequest = BpmModifyRequestDO.builder()
                .processInstanceId(task.getProcessInstanceId())
                .processDefinitionId(task.getProcessDefinitionId())
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .applicantUserId(userId)
                .acceptUserId(NumberUtils.parseLong(task.getAssignee()))
                .childProcessDefinitionKey(setting.getChildProcessDefinitionKey())
                .reasonType(reqVO.getReasonType())
                .reasonDetail(reqVO.getReasonDetail())
                .modifyPayloadJson(CollUtil.isEmpty(reqVO.getModifyPayload()) ? null : JsonUtils.toJsonString(reqVO.getModifyPayload()))
                .status(BpmModifyRequestStatusEnum.PENDING_ACCEPT.getStatus())
                .build();
        modifyRequestMapper.insert(modifyRequest);
        return modifyRequest.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptModifyRequest(Long userId, Long id, BpmModifyRequestHandleReqVO reqVO) {
        BpmModifyRequestDO modifyRequest = validatePendingModifyRequest(id);
        Task task = bpmTaskService.validateTask(userId, modifyRequest.getTaskId());
        ResolvedModifyRequestSetting setting = resolveModifyRequestSetting(processDefinitionService
                        .getProcessDefinitionInfo(task.getProcessDefinitionId()), task.getTaskDefinitionKey(),
                modifyRequest.getChildProcessDefinitionKey(), null);

        BpmTaskStartModifyChildReqVO startReqVO = new BpmTaskStartModifyChildReqVO();
        startReqVO.setId(task.getId());
        startReqVO.setChildProcessDefinitionKey(setting.getChildProcessDefinitionKey());
        startReqVO.setReasonType(modifyRequest.getReasonType());
        startReqVO.setReasonDetail(modifyRequest.getReasonDetail());
        startReqVO.setModifyPayload(buildModifyPayload(modifyRequest, userId));
        startReqVO.setResumeStrategy(setting.getResumeStrategy());
        BpmModifyChildProcessStartResultDTO startResult = modifyChildProcessService.startModifyChildProcess(userId,
                modifyRequest.getApplicantUserId(), startReqVO);

        BpmModifyRequestDO updateObj = new BpmModifyRequestDO();
        updateObj.setId(modifyRequest.getId());
        updateObj.setAcceptUserId(userId);
        updateObj.setAcceptReason(reqVO == null ? null : reqVO.getReason());
        updateObj.setParentChildLinkId(startResult.getLinkId());
        updateObj.setChildProcessInstanceId(startResult.getChildProcessInstanceId());
        updateObj.setStatus(BpmModifyRequestStatusEnum.MODIFYING.getStatus());
        modifyRequestMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectModifyRequest(Long userId, Long id, BpmModifyRequestHandleReqVO reqVO) {
        BpmModifyRequestDO modifyRequest = validatePendingModifyRequest(id);
        bpmTaskService.validateTask(userId, modifyRequest.getTaskId());

        BpmModifyRequestDO updateObj = new BpmModifyRequestDO();
        updateObj.setId(modifyRequest.getId());
        updateObj.setAcceptUserId(userId);
        updateObj.setRejectReason(reqVO == null ? null : reqVO.getReason());
        updateObj.setStatus(BpmModifyRequestStatusEnum.REJECTED.getStatus());
        modifyRequestMapper.updateById(updateObj);
    }

    @Override
    public List<BpmModifyRequestDO> getPendingModifyRequestListByTaskId(String taskId) {
        return modifyRequestMapper.selectListByTaskIdAndStatuses(taskId,
                List.of(BpmModifyRequestStatusEnum.PENDING_ACCEPT.getStatus()));
    }

    @Override
    public List<BpmModifyRequestDO> getModifyRequestListByProcessInstanceId(String processInstanceId) {
        return modifyRequestMapper.selectListByProcessInstanceId(processInstanceId);
    }

    public void updateModifyRequestCompletedByChildProcessInstanceId(String childProcessInstanceId) {
        BpmModifyRequestDO modifyRequest = modifyRequestMapper.selectByChildProcessInstanceId(childProcessInstanceId);
        if (modifyRequest == null) {
            return;
        }
        BpmModifyRequestDO updateObj = new BpmModifyRequestDO();
        updateObj.setId(modifyRequest.getId());
        updateObj.setStatus(BpmModifyRequestStatusEnum.COMPLETED.getStatus());
        modifyRequestMapper.updateById(updateObj);
    }

    private ProcessInstance validateProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = processInstanceService.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw exception(PROCESS_INSTANCE_NOT_EXISTS);
        }
        return processInstance;
    }

    private Task resolveActiveTask(String processInstanceId, String taskId) {
        if (StrUtil.isNotBlank(taskId)) {
            Task task = bpmTaskService.getTask(taskId);
            if (task == null || ObjectUtil.notEqual(task.getProcessInstanceId(), processInstanceId)) {
                throw exception(TASK_NOT_EXISTS);
            }
            return task;
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        if (CollUtil.size(tasks) != 1) {
            throw exception(TASK_MODIFY_REQUEST_ACTIVE_TASK_REQUIRED);
        }
        return tasks.get(0);
    }

    private void validateNoActiveModifyRequest(String processInstanceId) {
        if (modifyRequestMapper.selectActiveByProcessInstanceId(processInstanceId, ACTIVE_STATUSES) != null) {
            throw exception(TASK_MODIFY_REQUEST_ACTIVE_EXISTS);
        }
    }

    private BpmModifyRequestDO validatePendingModifyRequest(Long id) {
        BpmModifyRequestDO modifyRequest = modifyRequestMapper.selectById(id);
        if (modifyRequest == null) {
            throw exception(TASK_MODIFY_REQUEST_NOT_EXISTS);
        }
        if (ObjectUtil.notEqual(modifyRequest.getStatus(), BpmModifyRequestStatusEnum.PENDING_ACCEPT.getStatus())) {
            throw exception(TASK_MODIFY_REQUEST_STATUS_ERROR);
        }
        return modifyRequest;
    }

    private ResolvedModifyRequestSetting resolveModifyRequestSetting(BpmProcessDefinitionInfoDO processDefinitionInfo,
                                                                     String taskDefinitionKey,
                                                                     String fallbackChildProcessDefinitionKey,
                                                                     Integer fallbackResumeStrategy) {
        BpmSimpleModelNodeVO simpleModel = parseSimpleModel(processDefinitionInfo);
        BpmSimpleModelNodeVO.ModifyRequestSetting processSetting = simpleModel == null ? null : simpleModel.getModifyRequestSetting();
        if (Boolean.TRUE.equals(processSetting == null ? null : processSetting.getEnable())) {
            return ResolvedModifyRequestSetting.from(processSetting, fallbackChildProcessDefinitionKey, fallbackResumeStrategy);
        }

        BpmSimpleModelNodeVO currentNode = findNode(simpleModel, taskDefinitionKey);
        BpmSimpleModelNodeVO.ModifyProcessSetting nodeSetting = currentNode == null ? null : currentNode.getModifyProcessSetting();
        if (Boolean.TRUE.equals(nodeSetting == null ? null : nodeSetting.getEnable())) {
            return ResolvedModifyRequestSetting.from(nodeSetting, fallbackChildProcessDefinitionKey, fallbackResumeStrategy);
        }
        if (StrUtil.isNotBlank(fallbackChildProcessDefinitionKey)) {
            ResolvedModifyRequestSetting setting = new ResolvedModifyRequestSetting();
            setting.setEnable(true);
            setting.setNodeScopeType(NODE_SCOPE_ALL);
            setting.setChildProcessDefinitionKey(fallbackChildProcessDefinitionKey);
            setting.setResumeStrategy(ObjectUtil.defaultIfNull(fallbackResumeStrategy,
                    BpmModifyChildProcessResumeStrategyEnum.CONTINUE_LAST_ACTIVE_NODE.getType()));
            return setting;
        }
        throw exception(TASK_MODIFY_REQUEST_NOT_ENABLED);
    }

    private BpmSimpleModelNodeVO parseSimpleModel(BpmProcessDefinitionInfoDO processDefinitionInfo) {
        if (processDefinitionInfo == null || StrUtil.isBlank(processDefinitionInfo.getSimpleModel())) {
            return null;
        }
        return JsonUtils.parseObject(processDefinitionInfo.getSimpleModel(), BpmSimpleModelNodeVO.class);
    }

    private BpmSimpleModelNodeVO findNode(BpmSimpleModelNodeVO node, String nodeId) {
        if (node == null || StrUtil.isBlank(nodeId)) {
            return null;
        }
        if (StrUtil.equals(node.getId(), nodeId)) {
            return node;
        }
        if (CollUtil.isNotEmpty(node.getConditionNodes())) {
            for (BpmSimpleModelNodeVO child : node.getConditionNodes()) {
                BpmSimpleModelNodeVO match = findNode(child, nodeId);
                if (match != null) {
                    return match;
                }
            }
        }
        return findNode(node.getChildNode(), nodeId);
    }

    private void validateApplicant(ResolvedModifyRequestSetting setting, Long userId) {
        if (setting.getApplicantStrategy() == null) {
            return;
        }
        Set<Long> userIds = taskCandidateInvoker.calculateUsers(setting.getApplicantStrategy(), setting.getApplicantParam());
        if (!userIds.contains(userId)) {
            throw exception(TASK_MODIFY_REQUEST_APPLICANT_DENIED);
        }
    }

    private void validateNodeScope(ResolvedModifyRequestSetting setting, String taskDefinitionKey) {
        Integer scopeType = ObjectUtil.defaultIfNull(setting.getNodeScopeType(), NODE_SCOPE_ALL);
        List<String> nodeIds = setting.getNodeIds();
        if (scopeType == NODE_SCOPE_INCLUDE && !CollUtil.contains(nodeIds, taskDefinitionKey)) {
            throw exception(TASK_MODIFY_REQUEST_NODE_DENIED);
        }
        if (scopeType == NODE_SCOPE_EXCLUDE && CollUtil.contains(nodeIds, taskDefinitionKey)) {
            throw exception(TASK_MODIFY_REQUEST_NODE_DENIED);
        }
    }

    private void validateReasonType(ResolvedModifyRequestSetting setting, Integer reasonType) {
        if (reasonType == null || CollUtil.isEmpty(setting.getReasonTypes())) {
            return;
        }
        if (!setting.getReasonTypes().contains(reasonType)) {
            throw exception(TASK_MODIFY_REQUEST_NODE_DENIED);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseModifyPayload(String modifyPayloadJson) {
        if (StrUtil.isBlank(modifyPayloadJson)) {
            return null;
        }
        return JsonUtils.parseObject(modifyPayloadJson, Map.class);
    }

    private Map<String, Object> buildModifyPayload(BpmModifyRequestDO modifyRequest, Long acceptUserId) {
        Map<String, Object> payload = parseModifyPayload(modifyRequest.getModifyPayloadJson());
        if (payload == null) {
            payload = new HashMap<>();
        }
        payload.putIfAbsent(MODIFY_REQUEST_ID, modifyRequest.getId());
        payload.putIfAbsent(MODIFY_APPLICANT_USER_ID, modifyRequest.getApplicantUserId());
        payload.putIfAbsent(MODIFY_ACCEPT_USER_ID, acceptUserId);
        return payload;
    }

    @Data
    private static class ResolvedModifyRequestSetting {

        private Boolean enable;
        private Integer applicantStrategy;
        private String applicantParam;
        private Integer nodeScopeType;
        private List<String> nodeIds;
        private String childProcessDefinitionKey;
        private Integer resumeStrategy;
        private List<Integer> reasonTypes;

        static ResolvedModifyRequestSetting from(BpmSimpleModelNodeVO.ModifyRequestSetting setting,
                                                 String fallbackChildProcessDefinitionKey,
                                                 Integer fallbackResumeStrategy) {
            ResolvedModifyRequestSetting result = new ResolvedModifyRequestSetting();
            result.setEnable(setting.getEnable());
            result.setApplicantStrategy(setting.getApplicantStrategy());
            result.setApplicantParam(setting.getApplicantParam());
            result.setNodeScopeType(ObjectUtil.defaultIfNull(setting.getNodeScopeType(), NODE_SCOPE_ALL));
            result.setNodeIds(setting.getNodeIds());
            result.setChildProcessDefinitionKey(StrUtil.blankToDefault(setting.getChildProcessDefinitionKey(),
                    fallbackChildProcessDefinitionKey));
            result.setResumeStrategy(ObjectUtil.defaultIfNull(setting.getResumeStrategy(),
                    ObjectUtil.defaultIfNull(fallbackResumeStrategy,
                            BpmModifyChildProcessResumeStrategyEnum.CONTINUE_LAST_ACTIVE_NODE.getType())));
            result.setReasonTypes(setting.getReasonTypes());
            result.validate();
            return result;
        }

        static ResolvedModifyRequestSetting from(BpmSimpleModelNodeVO.ModifyProcessSetting setting,
                                                 String fallbackChildProcessDefinitionKey,
                                                 Integer fallbackResumeStrategy) {
            ResolvedModifyRequestSetting result = new ResolvedModifyRequestSetting();
            result.setEnable(setting.getEnable());
            result.setNodeScopeType(NODE_SCOPE_ALL);
            result.setChildProcessDefinitionKey(StrUtil.blankToDefault(setting.getChildProcessDefinitionKey(),
                    fallbackChildProcessDefinitionKey));
            result.setResumeStrategy(ObjectUtil.defaultIfNull(setting.getResumeStrategy(),
                    ObjectUtil.defaultIfNull(fallbackResumeStrategy,
                            BpmModifyChildProcessResumeStrategyEnum.CONTINUE_LAST_ACTIVE_NODE.getType())));
            result.setReasonTypes(setting.getReasonTypes());
            result.validate();
            return result;
        }

        private void validate() {
            if (StrUtil.isBlank(childProcessDefinitionKey)) {
                throw exception(TASK_MODIFY_REQUEST_NOT_ENABLED);
            }
        }

    }

}

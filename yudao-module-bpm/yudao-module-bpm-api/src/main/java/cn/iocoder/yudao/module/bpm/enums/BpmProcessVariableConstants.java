package cn.iocoder.yudao.module.bpm.enums;

/**
 * BPM 流程变量常量
 * 
 * 定义流程中通用的变量名称，用于在待办列表中显示关键业务信息
 * 
  */
public interface BpmProcessVariableConstants {

    /**
     * 单据编号变量名
     * 
     * 用于在待办列表中显示业务单据的编号
     */
    String BILL_CODE = "billCode";

    /**
     * 事由/说明变量名
     * 
     * 用于在待办列表中显示业务事由或说明信息
     */
    String CAUSE = "cause";

    /**
     * 部门名称变量名
     * 
     * 用于在待办列表中显示申请人所属部门名称
     */
    String DEPT_NAME = "deptName";

    /**
     * 部门ID变量名
     * 
     * 用于在待办列表中显示申请人所属部门ID
     */
    String DEPT_ID = "deptId";

    /**
     * 公司名称变量名
     * 
     * 用于在待办列表中显示申请人所属公司名称
     */
    String COMPANY_NAME = "companyName";

    /**
     * 公司ID变量名
     * 
     * 用于在待办列表中显示申请人所属公司ID
     */
    String COMPANY_ID = "companyId";

    /**
     * 运行时修改子流程变量：主流程实例 ID
     */
    String PARENT_PROCESS_INSTANCE_ID = "parentProcessInstanceId";

    /**
     * 运行时修改子流程变量：主流程任务 ID
     */
    String PARENT_TASK_ID = "parentTaskId";

    /**
     * 运行时修改子流程变量：主流程任务定义 Key
     */
    String PARENT_TASK_DEFINITION_KEY = "parentTaskDefinitionKey";

    /**
     * 运行时修改子流程变量：主子流程关联 ID
     */
    String MODIFY_LINK_ID = "modifyLinkId";

    /**
     * 运行时修改子流程变量：冻结前最后活动节点
     */
    String LAST_ACTIVE_NODE = "lastActiveNode";

    /**
     * 运行时修改子流程变量：修改类型
     */
    String MODIFY_TYPE = "modifyType";

    /**
     * 运行时修改子流程变量：默认回退节点
     */
    String RETURN_NODE = "returnNode";

    /**
     * 运行时修改子流程变量：修改说明
     */
    String MODIFY_REASON_DETAIL = "modifyReasonDetail";

    /**
     * 运行时修改子流程变量：恢复策略
     */
    String MODIFY_RESUME_STRATEGY = "modifyResumeStrategy";

    /**
     * 驳回重走次数上限，默认由服务端兜底
     */
    String REJECT_REPLAY_MAX_COUNT = "rejectReplayMaxCount";

    /**
     * 驳回重走超限处理动作
     */
    String REJECT_REPLAY_OVER_LIMIT_ACTION = "rejectReplayOverLimitAction";

    /**
     * 驳回重走累计次数
     */
    String REJECT_REPLAY_COUNT = "rejectReplayCount";
}

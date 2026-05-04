package cn.iocoder.yudao.module.bpm.dal.dataobject.task;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程修改申请 DO。
 */
@TableName("bpm_modify_request")
@KeySequence("bpm_modify_request_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmModifyRequestDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 主流程实例 ID。
     */
    private String processInstanceId;

    /**
     * 主流程定义 ID。
     */
    private String processDefinitionId;

    /**
     * 申请时的当前任务 ID。
     */
    private String taskId;

    /**
     * 申请时的当前节点 Key。
     */
    private String taskDefinitionKey;

    /**
     * 申请人。
     */
    private Long applicantUserId;

    /**
     * 接受人，即当前任务审批人。
     */
    private Long acceptUserId;

    /**
     * 修改子流程定义 Key。
     */
    private String childProcessDefinitionKey;

    /**
     * 修改子流程实例 ID。
     */
    private String childProcessInstanceId;

    /**
     * 主子流程关联 ID。
     */
    private Long parentChildLinkId;

    /**
     * 原因分类。
     */
    private Integer reasonType;

    /**
     * 原因说明。
     */
    private String reasonDetail;

    /**
     * 修改参数 JSON。
     */
    private String modifyPayloadJson;

    /**
     * 状态，参见 BpmModifyRequestStatusEnum。
     */
    private Integer status;

    /**
     * 接受说明。
     */
    private String acceptReason;

    /**
     * 拒绝说明。
     */
    private String rejectReason;

}

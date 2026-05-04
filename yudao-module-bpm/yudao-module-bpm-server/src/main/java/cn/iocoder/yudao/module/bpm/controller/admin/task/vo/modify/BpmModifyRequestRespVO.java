package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 流程修改申请 Response VO")
@Data
public class BpmModifyRequestRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "流程实例 ID")
    private String processInstanceId;

    @Schema(description = "流程定义 ID")
    private String processDefinitionId;

    @Schema(description = "任务 ID")
    private String taskId;

    @Schema(description = "任务节点 Key")
    private String taskDefinitionKey;

    @Schema(description = "申请人")
    private Long applicantUserId;

    @Schema(description = "接受人")
    private Long acceptUserId;

    @Schema(description = "修改子流程定义 Key")
    private String childProcessDefinitionKey;

    @Schema(description = "修改子流程实例 ID")
    private String childProcessInstanceId;

    @Schema(description = "主子流程关联 ID")
    private Long parentChildLinkId;

    @Schema(description = "原因分类")
    private Integer reasonType;

    @Schema(description = "原因说明")
    private String reasonDetail;

    @Schema(description = "修改参数 JSON")
    private String modifyPayloadJson;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "接受说明")
    private String acceptReason;

    @Schema(description = "拒绝说明")
    private String rejectReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

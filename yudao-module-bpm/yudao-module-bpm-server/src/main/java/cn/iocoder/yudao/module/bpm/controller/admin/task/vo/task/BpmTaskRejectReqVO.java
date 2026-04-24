package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskRejectModeEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskRejectReasonTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 不通过流程任务的 Request VO")
@Data
public class BpmTaskRejectReqVO {

    @Schema(description = "任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "任务编号不能为空")
    private String id;

    @Schema(description = "审批意见", requiredMode = Schema.RequiredMode.REQUIRED, example = "不错不错！")
    private String reason;

    @Schema(description = "驳回模式", example = "2")
    @InEnum(BpmTaskRejectModeEnum.class)
    private Integer rejectMode;

    @Schema(description = "驳回目标任务 Key", example = "Activity_1")
    private String targetTaskDefinitionKey;

    @Schema(description = "驳回原因分类", example = "1")
    @InEnum(BpmTaskRejectReasonTypeEnum.class)
    private Integer rejectReasonType;

    @Schema(description = "驳回补充说明", example = "需要补充设计资料")
    private String rejectDetail;

    @Schema(description = "驳回时回写的流程变量")
    private Map<String, Object> variables;

}

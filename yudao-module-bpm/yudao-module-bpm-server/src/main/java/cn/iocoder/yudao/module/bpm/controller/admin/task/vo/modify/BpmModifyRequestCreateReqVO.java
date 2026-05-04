package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmModifyChildProcessResumeStrategyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 创建流程修改申请 Request VO")
@Data
public class BpmModifyRequestCreateReqVO {

    @Schema(description = "流程实例 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "流程实例 ID 不能为空")
    private String processInstanceId;

    @Schema(description = "任务 ID；存在多个活动任务时必填")
    private String taskId;

    @Schema(description = "原因分类")
    private Integer reasonType;

    @Schema(description = "原因说明", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原因说明不能为空")
    private String reasonDetail;

    @Schema(description = "修改参数")
    private Map<String, Object> modifyPayload;

    @Schema(description = "客户端指定的修改子流程 Key；后端优先使用流程配置")
    private String childProcessDefinitionKey;

    @Schema(description = "客户端指定恢复策略；后端优先使用流程配置")
    @InEnum(BpmModifyChildProcessResumeStrategyEnum.class)
    private Integer resumeStrategy;

}

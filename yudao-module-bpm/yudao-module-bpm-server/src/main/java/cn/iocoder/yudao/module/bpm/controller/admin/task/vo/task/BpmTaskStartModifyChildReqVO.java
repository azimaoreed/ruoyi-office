package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmModifyChildProcessResumeStrategyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 发起修改申请子流程 Request VO")
@Data
public class BpmTaskStartModifyChildReqVO {

    @Schema(description = "当前任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "当前任务编号不能为空")
    private String id;

    @Schema(description = "修改申请子流程定义 Key", requiredMode = Schema.RequiredMode.REQUIRED, example = "modify_design_process")
    @NotEmpty(message = "修改申请子流程定义 Key 不能为空")
    private String childProcessDefinitionKey;

    @Schema(description = "发起修改原因类型", example = "1")
    private Integer reasonType;

    @Schema(description = "发起修改说明", requiredMode = Schema.RequiredMode.REQUIRED, example = "需要补充图纸并重新校验")
    @NotEmpty(message = "发起修改说明不能为空")
    private String reasonDetail;

    @Schema(description = "子流程启动变量")
    private Map<String, Object> modifyPayload;

    @Schema(description = "子流程结束后的恢复策略", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "恢复策略不能为空")
    @InEnum(BpmModifyChildProcessResumeStrategyEnum.class)
    private Integer resumeStrategy;

}

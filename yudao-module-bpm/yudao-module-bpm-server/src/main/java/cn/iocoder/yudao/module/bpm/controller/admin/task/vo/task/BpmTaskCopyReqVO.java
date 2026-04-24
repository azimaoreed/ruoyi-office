package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Collection;

@Schema(description = "管理后台 - 抄送流程任务的 Request VO")
@Data
public class BpmTaskCopyReqVO {

    @Schema(description = "任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "任务编号不能为空")
    private String id;

    @Schema(description = "抄送的用户编号数组", example = "[1,2]")
    private Collection<Long> copyUserIds;

    @Schema(description = "抄送的角色编号数组", example = "[1,2]")
    private Collection<Long> copyRoleIds;

    @Schema(description = "抄送的部门编号数组", example = "[1,2]")
    private Collection<Long> copyDeptIds;

    @Schema(description = "抄送意见", example = "帮忙看看！")
    private String reason;

    @AssertTrue(message = "抄送对象不能为空")
    public boolean isCopyTargetPresent() {
        return hasValue(copyUserIds) || hasValue(copyRoleIds) || hasValue(copyDeptIds);
    }

    private boolean hasValue(Collection<Long> values) {
        return values != null && !values.isEmpty();
    }
}

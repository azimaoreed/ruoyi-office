package cn.iocoder.yudao.module.hrm.api.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "RPC 服务 - 员工精简信息 Response DTO")
@Data
public class EmployeeSimpleRespDTO {

    @Schema(description = "关联用户ID", example = "1")
    private Long userId;

    @Schema(description = "所属部门名称", example = "研发部")
    private String deptName;

    @Schema(description = "职位", example = "产品经理")
    private String jobPost;

    @Schema(description = "职务", example = "部门经理")
    private String jobPosition;

}

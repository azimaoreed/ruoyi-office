package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 处理流程修改申请 Request VO")
@Data
public class BpmModifyRequestHandleReqVO {

    @Schema(description = "处理说明")
    private String reason;

}

package cn.iocoder.yudao.module.bpm.service.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改申请子流程启动结果。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BpmModifyChildProcessStartResultDTO {

    private Long linkId;

    private String childProcessInstanceId;

}

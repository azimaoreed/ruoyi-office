package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskStartModifyChildReqVO;
import jakarta.validation.Valid;

/**
 * 修改申请子流程 Service 接口
 */
public interface BpmModifyChildProcessService {

    /**
     * 发起修改申请子流程。
     *
     * @param userId 用户编号
     * @param reqVO 请求参数
     */
    void startModifyChildProcess(Long userId, @Valid BpmTaskStartModifyChildReqVO reqVO);

}

package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskStartModifyChildReqVO;
import jakarta.validation.Valid;
import org.flowable.engine.runtime.ProcessInstance;

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

    /**
     * 处理运行时修改子流程完成后的主流程恢复。
     *
     * @param childProcessInstance 子流程实例
     * @param status 子流程状态
     * @param reason 子流程原因
     * @return 是否命中运行时修改子流程
     */
    boolean handleChildProcessCompleted(ProcessInstance childProcessInstance, Integer status, String reason);

}

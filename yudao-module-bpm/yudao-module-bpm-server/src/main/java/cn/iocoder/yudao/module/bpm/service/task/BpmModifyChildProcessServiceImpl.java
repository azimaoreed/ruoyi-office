package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskStartModifyChildReqVO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.TASK_MODIFY_CHILD_PROCESS_NOT_SUPPORTED;

/**
 * 修改申请子流程 Service 实现类
 */
@Service
@Validated
public class BpmModifyChildProcessServiceImpl implements BpmModifyChildProcessService {

    @Override
    public void startModifyChildProcess(Long userId, BpmTaskStartModifyChildReqVO reqVO) {
        // TASK-07 先补齐接口骨架，实际启动逻辑在 TASK-09/TASK-10 中实现。
        throw exception(TASK_MODIFY_CHILD_PROCESS_NOT_SUPPORTED);
    }

}

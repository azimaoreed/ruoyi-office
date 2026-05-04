package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify.BpmModifyRequestCreateReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify.BpmModifyRequestHandleReqVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmModifyRequestDO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 流程修改申请 Service 接口。
 */
public interface BpmModifyRequestService {

    Long createModifyRequest(Long userId, @Valid BpmModifyRequestCreateReqVO reqVO);

    void acceptModifyRequest(Long userId, @NotNull(message = "修改申请 ID 不能为空") Long id,
                             @Valid BpmModifyRequestHandleReqVO reqVO);

    void rejectModifyRequest(Long userId, @NotNull(message = "修改申请 ID 不能为空") Long id,
                             @Valid BpmModifyRequestHandleReqVO reqVO);

    List<BpmModifyRequestDO> getPendingModifyRequestListByTaskId(@NotEmpty(message = "任务 ID 不能为空") String taskId);

    List<BpmModifyRequestDO> getModifyRequestListByProcessInstanceId(@NotEmpty(message = "流程实例 ID 不能为空")
                                                                     String processInstanceId);

}

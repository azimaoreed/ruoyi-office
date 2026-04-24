package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmRejectHistoryDO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 驳回历史 Service 接口
 */
public interface BpmRejectHistoryService {

    /**
     * 创建驳回历史。
     *
     * @param rejectHistory 驳回历史
     * @return 驳回历史
     */
    BpmRejectHistoryDO createRejectHistory(@Valid BpmRejectHistoryDO rejectHistory);

    /**
     * 查询流程实例下的驳回历史。
     *
     * @param processInstanceId 流程实例编号
     * @return 驳回历史列表
     */
    List<BpmRejectHistoryDO> getRejectHistoryList(@NotEmpty(message = "流程实例编号不能为空") String processInstanceId);

}

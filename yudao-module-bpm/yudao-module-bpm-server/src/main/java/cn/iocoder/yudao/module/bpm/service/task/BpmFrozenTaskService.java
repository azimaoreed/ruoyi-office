package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmFrozenTaskDO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 冻结任务 Service 接口
 */
public interface BpmFrozenTaskService {

    /**
     * 创建冻结任务记录。
     *
     * @param frozenTask 冻结任务
     * @return 冻结任务
     */
    BpmFrozenTaskDO createFrozenTask(@Valid BpmFrozenTaskDO frozenTask);

    /**
     * 查询流程实例下的冻结任务列表。
     *
     * @param processInstanceId 流程实例 ID
     * @return 冻结任务列表
     */
    List<BpmFrozenTaskDO> getFrozenTaskList(@NotEmpty(message = "流程实例 ID 不能为空") String processInstanceId);

    /**
     * 判断流程实例是否存在激活的冻结任务。
     *
     * @param processInstanceId 流程实例 ID
     * @param status 冻结状态
     * @return 是否存在
     */
    Boolean existsActiveFrozenTask(@NotEmpty(message = "流程实例 ID 不能为空") String processInstanceId, Integer status);

    /**
     * 按关联记录更新冻结状态。
     *
     * @param linkId 关联记录 ID
     * @param status 冻结状态
     */
    void updateFrozenTaskStatusByLinkId(@NotNull(message = "关联记录 ID 不能为空") Long linkId, Integer status);

}

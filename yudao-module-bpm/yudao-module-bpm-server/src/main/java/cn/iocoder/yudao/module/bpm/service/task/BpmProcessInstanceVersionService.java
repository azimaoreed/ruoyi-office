package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmProcessInstanceVersionDO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 流程实例版本 Service 接口
 */
public interface BpmProcessInstanceVersionService {

    /**
     * 初始化流程实例版本。
     *
     * @param processInstanceId 流程实例编号
     * @return 当前版本
     */
    BpmProcessInstanceVersionDO createInitialVersionIfAbsent(@NotEmpty(message = "流程实例编号不能为空") String processInstanceId);

    /**
     * 获取当前进行中的流程版本。
     *
     * @param processInstanceId 流程实例编号
     * @return 当前版本
     */
    BpmProcessInstanceVersionDO getCurrentVersion(@NotEmpty(message = "流程实例编号不能为空") String processInstanceId);

    /**
     * 获取流程实例的所有版本。
     *
     * @param processInstanceId 流程实例编号
     * @return 版本列表
     */
    List<BpmProcessInstanceVersionDO> getVersionList(@NotEmpty(message = "流程实例编号不能为空") String processInstanceId);

    /**
     * 归档当前版本并创建下一个版本。
     *
     * @param processInstanceId 流程实例编号
     * @param sourceRejectId 来源驳回记录编号
     * @return 新版本
     */
    BpmProcessInstanceVersionDO createNextVersion(@NotEmpty(message = "流程实例编号不能为空") String processInstanceId,
                                                  Long sourceRejectId);

    /**
     * 更新当前版本状态。
     *
     * @param processInstanceId 流程实例编号
     * @param versionStatus 目标状态
     */
    void markCurrentVersionStatus(@NotEmpty(message = "流程实例编号不能为空") String processInstanceId,
                                  @NotNull(message = "版本状态不能为空") Integer versionStatus);

}

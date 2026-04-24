package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmParentChildProcessLinkDO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 主子流程关联 Service 接口
 */
public interface BpmParentChildProcessLinkService {

    /**
     * 创建主子流程关联。
     *
     * @param link 关联记录
     * @return 关联记录
     */
    BpmParentChildProcessLinkDO createLink(@Valid BpmParentChildProcessLinkDO link);

    /**
     * 查询子流程实例对应的主子流程关联。
     *
     * @param childProcessInstanceId 子流程实例 ID
     * @return 关联记录
     */
    BpmParentChildProcessLinkDO getLinkByChildProcessInstanceId(@NotEmpty(message = "子流程实例 ID 不能为空") String childProcessInstanceId);

    /**
     * 查询主流程实例下的所有关联记录。
     *
     * @param parentProcessInstanceId 主流程实例 ID
     * @return 关联记录列表
     */
    List<BpmParentChildProcessLinkDO> getLinkListByParentProcessInstanceId(@NotEmpty(message = "主流程实例 ID 不能为空") String parentProcessInstanceId);

}

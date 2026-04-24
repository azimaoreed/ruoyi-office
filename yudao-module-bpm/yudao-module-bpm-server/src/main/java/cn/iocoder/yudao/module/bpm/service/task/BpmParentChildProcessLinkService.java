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
     * 获取主子流程关联。
     *
     * @param id 关联记录 ID
     * @return 关联记录
     */
    BpmParentChildProcessLinkDO getLink(Long id);

    /**
     * 回填子流程实例 ID。
     *
     * @param id 关联记录 ID
     * @param childProcessInstanceId 子流程实例 ID
     */
    void updateChildProcessInstanceId(Long id,
                                      @NotEmpty(message = "子流程实例 ID 不能为空") String childProcessInstanceId);

    /**
     * 更新关联处理结果。
     *
     * @param id 关联记录 ID
     * @param status 状态
     * @param resultJson 结果 JSON
     */
    void updateLinkResult(Long id, Integer status, String resultJson);

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

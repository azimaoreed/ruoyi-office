package cn.iocoder.yudao.module.bpm.dal.dataobject.task;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主子流程关联 DO
 */
@TableName("bpm_parent_child_process_link")
@KeySequence("bpm_parent_child_process_link_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmParentChildProcessLinkDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 主流程实例 ID
     */
    private String parentProcessInstanceId;

    /**
     * 发起修改时的主流程任务 ID
     */
    private String parentTaskId;

    /**
     * 发起修改时所在节点
     */
    private String parentTaskDefinitionKey;

    /**
     * 子流程实例 ID
     */
    private String childProcessInstanceId;

    /**
     * 子流程定义 Key
     */
    private String childProcessDefinitionKey;

    /**
     * 冻结前最后活动节点
     */
    private String lastActiveNodeKey;

    /**
     * 恢复策略
     */
    private Integer resumeStrategy;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 子流程处理结果 JSON
     */
    private String resultJson;

}

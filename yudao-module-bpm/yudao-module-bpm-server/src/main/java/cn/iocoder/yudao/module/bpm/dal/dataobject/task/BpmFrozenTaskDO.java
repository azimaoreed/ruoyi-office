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
 * 冻结任务 DO
 */
@TableName("bpm_frozen_task")
@KeySequence("bpm_frozen_task_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmFrozenTaskDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 流程实例 ID
     */
    private String processInstanceId;

    /**
     * 冻结任务 ID
     */
    private String taskId;

    /**
     * 节点 Key
     */
    private String taskDefinitionKey;

    /**
     * 当前处理人
     */
    private Long assigneeUserId;

    /**
     * 冻结原因
     */
    private String freezeReason;

    /**
     * 关联主子流程关系 ID
     */
    private Long linkId;

    /**
     * 状态
     */
    private Integer status;

}

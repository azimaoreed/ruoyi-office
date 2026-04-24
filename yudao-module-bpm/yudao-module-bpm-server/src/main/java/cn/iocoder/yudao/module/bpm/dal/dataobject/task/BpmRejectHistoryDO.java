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
 * 任务驳回历史 DO
 */
@TableName("bpm_reject_history")
@KeySequence("bpm_reject_history_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmRejectHistoryDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 流程实例编号
     */
    private String processInstanceId;

    /**
     * 发起驳回的任务编号
     */
    private String taskId;

    /**
     * 来源节点定义 key
     */
    private String sourceTaskDefinitionKey;

    /**
     * 目标节点定义 key
     */
    private String targetTaskDefinitionKey;

    /**
     * 驳回模式
     */
    private Integer rejectMode;

    /**
     * 驳回原因分类
     */
    private Integer rejectReasonType;

    /**
     * 驳回说明
     */
    private String rejectDetail;

    /**
     * 驳回前版本号
     */
    private Integer fromVersionNo;

    /**
     * 驳回后版本号
     */
    private Integer toVersionNo;

}

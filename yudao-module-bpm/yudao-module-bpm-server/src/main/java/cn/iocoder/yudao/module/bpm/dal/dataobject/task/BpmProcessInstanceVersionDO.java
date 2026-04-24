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
 * 流程实例版本 DO
 */
@TableName("bpm_process_instance_version")
@KeySequence("bpm_process_instance_version_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessInstanceVersionDO extends BaseDO {

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
     * 版本号
     */
    private Integer versionNo;

    /**
     * 版本状态
     */
    private Integer versionStatus;

    /**
     * 来源驳回记录编号
     */
    private Long sourceRejectId;

}

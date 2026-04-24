package cn.iocoder.yudao.module.bpm.dal.mysql.task;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmFrozenTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BpmFrozenTaskMapper extends BaseMapperX<BpmFrozenTaskDO> {

    default List<BpmFrozenTaskDO> selectListByProcessInstanceId(String processInstanceId) {
        return selectList(new LambdaQueryWrapperX<BpmFrozenTaskDO>()
                .eq(BpmFrozenTaskDO::getProcessInstanceId, processInstanceId)
                .orderByAsc(BpmFrozenTaskDO::getId));
    }

    default Boolean existsActiveByProcessInstanceId(String processInstanceId, Integer status) {
        return selectCount(new LambdaQueryWrapperX<BpmFrozenTaskDO>()
                .eq(BpmFrozenTaskDO::getProcessInstanceId, processInstanceId)
                .eq(BpmFrozenTaskDO::getStatus, status)) > 0;
    }

}

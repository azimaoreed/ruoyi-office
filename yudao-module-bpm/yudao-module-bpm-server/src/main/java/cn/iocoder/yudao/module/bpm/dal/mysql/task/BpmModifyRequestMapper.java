package cn.iocoder.yudao.module.bpm.dal.mysql.task;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmModifyRequestDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BpmModifyRequestMapper extends BaseMapperX<BpmModifyRequestDO> {

    default BpmModifyRequestDO selectByChildProcessInstanceId(String childProcessInstanceId) {
        return selectOne(new LambdaQueryWrapperX<BpmModifyRequestDO>()
                .eq(BpmModifyRequestDO::getChildProcessInstanceId, childProcessInstanceId)
                .orderByDesc(BpmModifyRequestDO::getId));
    }

    default List<BpmModifyRequestDO> selectListByTaskIdAndStatuses(String taskId, Collection<Integer> statuses) {
        return selectList(new LambdaQueryWrapperX<BpmModifyRequestDO>()
                .eq(BpmModifyRequestDO::getTaskId, taskId)
                .in(BpmModifyRequestDO::getStatus, statuses)
                .orderByDesc(BpmModifyRequestDO::getId));
    }

    default BpmModifyRequestDO selectActiveByProcessInstanceId(String processInstanceId, Collection<Integer> statuses) {
        return selectOne(new LambdaQueryWrapperX<BpmModifyRequestDO>()
                .eq(BpmModifyRequestDO::getProcessInstanceId, processInstanceId)
                .in(BpmModifyRequestDO::getStatus, statuses)
                .orderByDesc(BpmModifyRequestDO::getId)
                .last("LIMIT 1"));
    }

    default List<BpmModifyRequestDO> selectListByProcessInstanceId(String processInstanceId) {
        return selectList(new LambdaQueryWrapperX<BpmModifyRequestDO>()
                .eq(BpmModifyRequestDO::getProcessInstanceId, processInstanceId)
                .orderByDesc(BpmModifyRequestDO::getId));
    }

}

package cn.iocoder.yudao.module.bpm.dal.mysql.task;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmRejectHistoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BpmRejectHistoryMapper extends BaseMapperX<BpmRejectHistoryDO> {

    default List<BpmRejectHistoryDO> selectListByProcessInstanceId(String processInstanceId) {
        return selectList(new LambdaQueryWrapperX<BpmRejectHistoryDO>()
                .eq(BpmRejectHistoryDO::getProcessInstanceId, processInstanceId)
                .orderByAsc(BpmRejectHistoryDO::getId));
    }

}

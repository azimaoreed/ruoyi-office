package cn.iocoder.yudao.module.bpm.dal.mysql.task;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmProcessInstanceVersionDO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceVersionStatusEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BpmProcessInstanceVersionMapper extends BaseMapperX<BpmProcessInstanceVersionDO> {

    default BpmProcessInstanceVersionDO selectCurrentByProcessInstanceId(String processInstanceId) {
        return selectList(new LambdaQueryWrapperX<BpmProcessInstanceVersionDO>()
                .eq(BpmProcessInstanceVersionDO::getProcessInstanceId, processInstanceId)
                .eq(BpmProcessInstanceVersionDO::getVersionStatus,
                        BpmProcessInstanceVersionStatusEnum.RUNNING.getStatus())
                .orderByDesc(BpmProcessInstanceVersionDO::getVersionNo))
                .stream().findFirst().orElse(null);
    }

    default BpmProcessInstanceVersionDO selectLatestByProcessInstanceId(String processInstanceId) {
        return selectList(new LambdaQueryWrapperX<BpmProcessInstanceVersionDO>()
                .eq(BpmProcessInstanceVersionDO::getProcessInstanceId, processInstanceId)
                .orderByDesc(BpmProcessInstanceVersionDO::getVersionNo))
                .stream().findFirst().orElse(null);
    }

    default java.util.List<BpmProcessInstanceVersionDO> selectListByProcessInstanceId(String processInstanceId) {
        return selectList(new LambdaQueryWrapperX<BpmProcessInstanceVersionDO>()
                .eq(BpmProcessInstanceVersionDO::getProcessInstanceId, processInstanceId)
                .orderByAsc(BpmProcessInstanceVersionDO::getVersionNo));
    }

}

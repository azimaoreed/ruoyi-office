package cn.iocoder.yudao.module.bpm.dal.mysql.task;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmParentChildProcessLinkDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BpmParentChildProcessLinkMapper extends BaseMapperX<BpmParentChildProcessLinkDO> {

    default BpmParentChildProcessLinkDO selectByChildProcessInstanceId(String childProcessInstanceId) {
        return selectOne(new LambdaQueryWrapperX<BpmParentChildProcessLinkDO>()
                .eq(BpmParentChildProcessLinkDO::getChildProcessInstanceId, childProcessInstanceId)
                .orderByDesc(BpmParentChildProcessLinkDO::getId));
    }

    default List<BpmParentChildProcessLinkDO> selectListByParentProcessInstanceId(String parentProcessInstanceId) {
        return selectList(new LambdaQueryWrapperX<BpmParentChildProcessLinkDO>()
                .eq(BpmParentChildProcessLinkDO::getParentProcessInstanceId, parentProcessInstanceId)
                .orderByAsc(BpmParentChildProcessLinkDO::getId));
    }

}

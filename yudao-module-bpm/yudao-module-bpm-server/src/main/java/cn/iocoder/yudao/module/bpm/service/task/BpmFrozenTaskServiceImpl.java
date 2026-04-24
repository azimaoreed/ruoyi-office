package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmFrozenTaskDO;
import cn.iocoder.yudao.module.bpm.dal.mysql.task.BpmFrozenTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 冻结任务 Service 实现类
 */
@Service
@Validated
public class BpmFrozenTaskServiceImpl implements BpmFrozenTaskService {

    @Resource
    private BpmFrozenTaskMapper frozenTaskMapper;

    @Override
    public BpmFrozenTaskDO createFrozenTask(BpmFrozenTaskDO frozenTask) {
        frozenTaskMapper.insert(frozenTask);
        return frozenTask;
    }

    @Override
    public List<BpmFrozenTaskDO> getFrozenTaskList(String processInstanceId) {
        return frozenTaskMapper.selectListByProcessInstanceId(processInstanceId);
    }

    @Override
    public Boolean existsActiveFrozenTask(String processInstanceId, Integer status) {
        return frozenTaskMapper.existsActiveByProcessInstanceId(processInstanceId, status);
    }

}

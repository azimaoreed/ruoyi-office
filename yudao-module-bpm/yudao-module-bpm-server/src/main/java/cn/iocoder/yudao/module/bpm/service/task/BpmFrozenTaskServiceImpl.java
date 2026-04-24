package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmFrozenTaskDO;
import cn.iocoder.yudao.module.bpm.dal.mysql.task.BpmFrozenTaskMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

    @Override
    public void updateFrozenTaskStatusByLinkId(Long linkId, Integer status) {
        BpmFrozenTaskDO frozenTask = new BpmFrozenTaskDO();
        frozenTask.setStatus(status);
        frozenTaskMapper.update(frozenTask, new LambdaUpdateWrapper<BpmFrozenTaskDO>()
                .eq(BpmFrozenTaskDO::getLinkId, linkId));
    }

}

package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmRejectHistoryDO;
import cn.iocoder.yudao.module.bpm.dal.mysql.task.BpmRejectHistoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 驳回历史 Service 实现类
 */
@Service
@Validated
public class BpmRejectHistoryServiceImpl implements BpmRejectHistoryService {

    @Resource
    private BpmRejectHistoryMapper rejectHistoryMapper;

    @Override
    public BpmRejectHistoryDO createRejectHistory(BpmRejectHistoryDO rejectHistory) {
        rejectHistoryMapper.insert(rejectHistory);
        return rejectHistory;
    }

    @Override
    public List<BpmRejectHistoryDO> getRejectHistoryList(String processInstanceId) {
        return rejectHistoryMapper.selectListByProcessInstanceId(processInstanceId);
    }

    @Override
    public long countRejectHistory(String processInstanceId, Integer rejectMode) {
        return rejectHistoryMapper.selectCountByProcessInstanceIdAndRejectMode(processInstanceId, rejectMode);
    }

}

package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmParentChildProcessLinkDO;
import cn.iocoder.yudao.module.bpm.dal.mysql.task.BpmParentChildProcessLinkMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 主子流程关联 Service 实现类
 */
@Service
@Validated
public class BpmParentChildProcessLinkServiceImpl implements BpmParentChildProcessLinkService {

    @Resource
    private BpmParentChildProcessLinkMapper parentChildProcessLinkMapper;

    @Override
    public BpmParentChildProcessLinkDO createLink(BpmParentChildProcessLinkDO link) {
        parentChildProcessLinkMapper.insert(link);
        return link;
    }

    @Override
    public BpmParentChildProcessLinkDO getLinkByChildProcessInstanceId(String childProcessInstanceId) {
        return parentChildProcessLinkMapper.selectByChildProcessInstanceId(childProcessInstanceId);
    }

    @Override
    public List<BpmParentChildProcessLinkDO> getLinkListByParentProcessInstanceId(String parentProcessInstanceId) {
        return parentChildProcessLinkMapper.selectListByParentProcessInstanceId(parentProcessInstanceId);
    }

}

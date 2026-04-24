package cn.iocoder.yudao.module.bpm.service.task;

import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmProcessInstanceVersionDO;
import cn.iocoder.yudao.module.bpm.dal.mysql.task.BpmProcessInstanceVersionMapper;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceVersionStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * 流程实例版本 Service 实现类
 */
@Service
@Validated
public class BpmProcessInstanceVersionServiceImpl implements BpmProcessInstanceVersionService {

    @Resource
    private BpmProcessInstanceVersionMapper processInstanceVersionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BpmProcessInstanceVersionDO createInitialVersionIfAbsent(String processInstanceId) {
        BpmProcessInstanceVersionDO currentVersion = processInstanceVersionMapper
                .selectCurrentByProcessInstanceId(processInstanceId);
        if (currentVersion != null) {
            return currentVersion;
        }
        BpmProcessInstanceVersionDO latestVersion = processInstanceVersionMapper
                .selectLatestByProcessInstanceId(processInstanceId);
        if (latestVersion != null) {
            return latestVersion;
        }
        BpmProcessInstanceVersionDO version = BpmProcessInstanceVersionDO.builder()
                .processInstanceId(processInstanceId)
                .versionNo(1)
                .versionStatus(BpmProcessInstanceVersionStatusEnum.RUNNING.getStatus())
                .build();
        processInstanceVersionMapper.insert(version);
        return version;
    }

    @Override
    public BpmProcessInstanceVersionDO getCurrentVersion(String processInstanceId) {
        return processInstanceVersionMapper.selectCurrentByProcessInstanceId(processInstanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BpmProcessInstanceVersionDO createNextVersion(String processInstanceId, Long sourceRejectId) {
        BpmProcessInstanceVersionDO currentVersion = createInitialVersionIfAbsent(processInstanceId);
        if (Objects.equals(currentVersion.getVersionStatus(), BpmProcessInstanceVersionStatusEnum.RUNNING.getStatus())) {
            currentVersion.setVersionStatus(BpmProcessInstanceVersionStatusEnum.HISTORY.getStatus());
            processInstanceVersionMapper.updateById(currentVersion);
        }
        BpmProcessInstanceVersionDO nextVersion = BpmProcessInstanceVersionDO.builder()
                .processInstanceId(processInstanceId)
                .versionNo(currentVersion.getVersionNo() + 1)
                .versionStatus(BpmProcessInstanceVersionStatusEnum.RUNNING.getStatus())
                .sourceRejectId(sourceRejectId)
                .build();
        processInstanceVersionMapper.insert(nextVersion);
        return nextVersion;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markCurrentVersionStatus(String processInstanceId, Integer versionStatus) {
        BpmProcessInstanceVersionDO currentVersion = processInstanceVersionMapper
                .selectCurrentByProcessInstanceId(processInstanceId);
        if (currentVersion == null) {
            BpmProcessInstanceVersionDO latestVersion = processInstanceVersionMapper
                    .selectLatestByProcessInstanceId(processInstanceId);
            if (latestVersion == null) {
                BpmProcessInstanceVersionDO version = BpmProcessInstanceVersionDO.builder()
                        .processInstanceId(processInstanceId)
                        .versionNo(1)
                        .versionStatus(versionStatus)
                        .build();
                processInstanceVersionMapper.insert(version);
                return;
            }
            currentVersion = latestVersion;
        }
        if (Objects.equals(currentVersion.getVersionStatus(), versionStatus)) {
            return;
        }
        currentVersion.setVersionStatus(versionStatus);
        processInstanceVersionMapper.updateById(currentVersion);
    }

}

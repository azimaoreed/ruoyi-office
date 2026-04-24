package cn.iocoder.yudao.module.bpm.service.task.support;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskRejectLoopLimitActionEnum;

/**
 * 驳回重走循环保护器
 */
public final class BpmRejectReplayLoopGuard {

    public static final int DEFAULT_MAX_COUNT = 3;

    private BpmRejectReplayLoopGuard() {
    }

    public static int resolveMaxCount(Integer maxCount) {
        return maxCount == null ? DEFAULT_MAX_COUNT : maxCount;
    }

    public static Decision decide(long replayRejectCount, Integer maxCount, Integer actionValue) {
        int effectiveMaxCount = resolveMaxCount(maxCount);
        if (effectiveMaxCount <= 0 || replayRejectCount < effectiveMaxCount) {
            return Decision.PASS;
        }
        BpmTaskRejectLoopLimitActionEnum action = ObjectUtils.defaultIfNull(
                BpmTaskRejectLoopLimitActionEnum.typeOf(actionValue), BpmTaskRejectLoopLimitActionEnum.BLOCK);
        return action == BpmTaskRejectLoopLimitActionEnum.TRANSFER_ADMIN ? Decision.TRANSFER_ADMIN : Decision.BLOCK;
    }

    public enum Decision {

        PASS,
        BLOCK,
        TRANSFER_ADMIN

    }

}

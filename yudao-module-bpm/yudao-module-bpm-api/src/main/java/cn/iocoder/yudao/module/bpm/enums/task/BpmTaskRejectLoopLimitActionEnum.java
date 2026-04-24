package cn.iocoder.yudao.module.bpm.enums.task;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 驳回重走超限处理动作
 */
@Getter
@AllArgsConstructor
public enum BpmTaskRejectLoopLimitActionEnum implements ArrayValuable<Integer> {

    BLOCK(1, "阻断重走"),
    TRANSFER_ADMIN(2, "转交流程管理员");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmTaskRejectLoopLimitActionEnum::getAction)
            .toArray(Integer[]::new);

    private final Integer action;
    private final String name;

    public static BpmTaskRejectLoopLimitActionEnum typeOf(Integer action) {
        return Arrays.stream(values())
                .filter(bean -> bean.getAction().equals(action))
                .findAny()
                .orElse(null);
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

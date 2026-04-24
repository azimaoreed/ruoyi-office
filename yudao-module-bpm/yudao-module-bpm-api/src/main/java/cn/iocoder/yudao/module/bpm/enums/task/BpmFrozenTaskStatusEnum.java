package cn.iocoder.yudao.module.bpm.enums.task;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 冻结任务状态枚举
 */
@Getter
@AllArgsConstructor
public enum BpmFrozenTaskStatusEnum implements ArrayValuable<Integer> {

    FROZEN(1, "冻结中"),
    RESUMED(2, "已恢复");

    private final Integer status;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmFrozenTaskStatusEnum::getStatus)
            .toArray(Integer[]::new);

    public static BpmFrozenTaskStatusEnum statusOf(Integer status) {
        return ArrayUtil.firstMatch(item -> item.getStatus().equals(status), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

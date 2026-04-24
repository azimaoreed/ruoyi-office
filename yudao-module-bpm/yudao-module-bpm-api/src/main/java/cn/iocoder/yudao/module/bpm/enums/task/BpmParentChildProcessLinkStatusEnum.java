package cn.iocoder.yudao.module.bpm.enums.task;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 主子流程关联状态枚举
 */
@Getter
@AllArgsConstructor
public enum BpmParentChildProcessLinkStatusEnum implements ArrayValuable<Integer> {

    RUNNING(1, "运行中"),
    RESUMED(2, "已恢复"),
    CANCELLED(3, "已取消");

    private final Integer status;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmParentChildProcessLinkStatusEnum::getStatus)
            .toArray(Integer[]::new);

    public static BpmParentChildProcessLinkStatusEnum statusOf(Integer status) {
        return ArrayUtil.firstMatch(item -> item.getStatus().equals(status), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

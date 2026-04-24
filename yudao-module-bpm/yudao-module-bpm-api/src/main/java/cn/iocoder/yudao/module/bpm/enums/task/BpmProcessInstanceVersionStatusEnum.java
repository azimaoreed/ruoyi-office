package cn.iocoder.yudao.module.bpm.enums.task;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 流程实例版本状态枚举
 */
@Getter
@AllArgsConstructor
public enum BpmProcessInstanceVersionStatusEnum implements ArrayValuable<Integer> {

    RUNNING(1, "进行中"),
    HISTORY(2, "历史版本"),
    FINISHED(3, "已结束");

    private final Integer status;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmProcessInstanceVersionStatusEnum::getStatus)
            .toArray(Integer[]::new);

    public static BpmProcessInstanceVersionStatusEnum valueOfStatus(Integer status) {
        return ArrayUtil.firstMatch(item -> item.getStatus().equals(status), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

package cn.iocoder.yudao.module.bpm.enums.task;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 任务驳回模式枚举
 */
@Getter
@AllArgsConstructor
public enum BpmTaskRejectModeEnum implements ArrayValuable<Integer> {

    FINISH_PROCESS(1, "终止流程"),
    RETURN_AND_REPLAY(2, "退回重走"),
    CONTINUE_AFTER_MODIFY(3, "修改后继续");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmTaskRejectModeEnum::getType)
            .toArray(Integer[]::new);

    public static BpmTaskRejectModeEnum typeOf(Integer type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

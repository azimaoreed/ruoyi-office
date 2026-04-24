package cn.iocoder.yudao.module.bpm.enums.task;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 任务驳回原因分类枚举
 */
@Getter
@AllArgsConstructor
public enum BpmTaskRejectReasonTypeEnum implements ArrayValuable<Integer> {

    SUPPLEMENT(1, "补充资料"),
    MODIFY(2, "修改调整"),
    RISK(3, "风险校正"),
    OTHER(4, "其他");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmTaskRejectReasonTypeEnum::getType)
            .toArray(Integer[]::new);

    public static BpmTaskRejectReasonTypeEnum typeOf(Integer type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

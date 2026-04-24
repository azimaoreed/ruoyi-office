package cn.iocoder.yudao.module.bpm.enums.definition;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户任务驳回目标类型枚举
 */
@Getter
@AllArgsConstructor
public enum BpmUserTaskRejectTargetTypeEnum implements ArrayValuable<Integer> {

    FIXED_NODE(1, "固定节点"),
    RUNTIME_SELECTABLE(2, "运行时选择"),
    EXPRESSION_NODE(3, "表达式节点");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmUserTaskRejectTargetTypeEnum::getType)
            .toArray(Integer[]::new);

    public static BpmUserTaskRejectTargetTypeEnum typeOf(Integer type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

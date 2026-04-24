package cn.iocoder.yudao.module.bpm.enums.task;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 修改申请子流程恢复策略枚举
 */
@Getter
@AllArgsConstructor
public enum BpmModifyChildProcessResumeStrategyEnum implements ArrayValuable<Integer> {

    CONTINUE_LAST_ACTIVE_NODE(1, "继续冻结节点后续流程"),
    RETURN_TO_TARGET_NODE(2, "回退到目标节点重走");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmModifyChildProcessResumeStrategyEnum::getType)
            .toArray(Integer[]::new);

    public static BpmModifyChildProcessResumeStrategyEnum typeOf(Integer type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

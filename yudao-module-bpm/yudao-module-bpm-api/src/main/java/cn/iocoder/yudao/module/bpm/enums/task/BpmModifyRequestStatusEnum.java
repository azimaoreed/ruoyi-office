package cn.iocoder.yudao.module.bpm.enums.task;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 修改申请状态枚举。
 */
@Getter
@AllArgsConstructor
public enum BpmModifyRequestStatusEnum implements ArrayValuable<Integer> {

    PENDING_ACCEPT(1, "待接受"),
    REJECTED(2, "已拒绝"),
    MODIFYING(3, "修改中"),
    COMPLETED(4, "已完成"),
    CANCELED(5, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(BpmModifyRequestStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

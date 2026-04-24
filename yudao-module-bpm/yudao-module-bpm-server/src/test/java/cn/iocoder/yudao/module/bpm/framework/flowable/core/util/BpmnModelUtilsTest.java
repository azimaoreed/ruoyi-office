package cn.iocoder.yudao.module.bpm.framework.flowable.core.util;

import cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.model.simple.BpmSimpleModelNodeVO;
import cn.iocoder.yudao.module.bpm.enums.definition.BpmUserTaskRejectHandlerTypeEnum;
import cn.iocoder.yudao.module.bpm.enums.definition.BpmUserTaskRejectTargetTypeEnum;
import org.flowable.bpmn.model.UserTask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link BpmnModelUtils} 的单元测试
 */
public class BpmnModelUtilsTest {

    @Test
    public void testAddTaskRejectElements_parseExpressionConfig() {
        UserTask userTask = new UserTask();
        BpmSimpleModelNodeVO.RejectHandler rejectHandler = new BpmSimpleModelNodeVO.RejectHandler();
        rejectHandler.setType(BpmUserTaskRejectHandlerTypeEnum.RETURN_AND_REPLAY.getType());
        rejectHandler.setTargetType(BpmUserTaskRejectTargetTypeEnum.EXPRESSION_NODE.getType());
        rejectHandler.setReturnNodeId("Activity_Default");
        rejectHandler.setReturnNodeExpression("modifyType == 'filing_related' ? 'Activity_Filing' : 'Activity_Default'");
        rejectHandler.setReasonTypes(List.of(1, 2, 3));

        BpmnModelUtils.addTaskRejectElements(rejectHandler, userTask);

        assertEquals(BpmUserTaskRejectHandlerTypeEnum.RETURN_AND_REPLAY, BpmnModelUtils.parseRejectHandlerType(userTask));
        assertEquals(BpmUserTaskRejectTargetTypeEnum.EXPRESSION_NODE, BpmnModelUtils.parseRejectTargetType(userTask));
        assertEquals("Activity_Default", BpmnModelUtils.parseReturnTaskId(userTask));
        assertEquals("modifyType == 'filing_related' ? 'Activity_Filing' : 'Activity_Default'",
                BpmnModelUtils.parseReturnTaskExpression(userTask));
        assertEquals(List.of(1, 2, 3), BpmnModelUtils.parseRejectReasonTypes(userTask));
    }

}

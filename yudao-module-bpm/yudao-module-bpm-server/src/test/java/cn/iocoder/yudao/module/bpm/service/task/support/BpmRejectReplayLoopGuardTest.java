package cn.iocoder.yudao.module.bpm.service.task.support;

import org.junit.jupiter.api.Test;

import static cn.iocoder.yudao.module.bpm.service.task.support.BpmRejectReplayLoopGuard.Decision.BLOCK;
import static cn.iocoder.yudao.module.bpm.service.task.support.BpmRejectReplayLoopGuard.Decision.PASS;
import static cn.iocoder.yudao.module.bpm.service.task.support.BpmRejectReplayLoopGuard.Decision.TRANSFER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BpmRejectReplayLoopGuardTest {

    @Test
    void decide_whenBelowLimit_returnsPass() {
        assertEquals(PASS, BpmRejectReplayLoopGuard.decide(2, 3, null));
    }

    @Test
    void decide_whenLimitDisabled_returnsPass() {
        assertEquals(PASS, BpmRejectReplayLoopGuard.decide(5, 0, 1));
    }

    @Test
    void decide_whenReachedDefaultLimit_returnsBlock() {
        assertEquals(BLOCK, BpmRejectReplayLoopGuard.decide(3, null, null));
    }

    @Test
    void decide_whenReachedLimitAndConfiguredTransfer_returnsTransferAdmin() {
        assertEquals(TRANSFER_ADMIN, BpmRejectReplayLoopGuard.decide(3, 3, 2));
    }

}

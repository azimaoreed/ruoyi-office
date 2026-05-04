package cn.iocoder.yudao.module.bpm.controller.admin.task;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify.BpmModifyRequestCreateReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify.BpmModifyRequestHandleReqVO;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.modify.BpmModifyRequestRespVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmModifyRequestDO;
import cn.iocoder.yudao.module.bpm.service.task.BpmModifyRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 流程修改申请")
@RestController
@RequestMapping("/bpm/modify-request")
@Validated
public class BpmModifyRequestController {

    @Resource
    private BpmModifyRequestService modifyRequestService;

    @PostMapping("/create")
    @Operation(summary = "提交修改申请")
    @PreAuthorize("@ss.hasPermission('bpm:task:query')")
    public CommonResult<Long> createModifyRequest(@Valid @RequestBody BpmModifyRequestCreateReqVO reqVO) {
        return success(modifyRequestService.createModifyRequest(getLoginUserId(), reqVO));
    }

    @GetMapping("/pending-by-task")
    @Operation(summary = "查询任务下待接受的修改申请")
    @Parameter(name = "taskId", description = "任务 ID", required = true)
    @PreAuthorize("@ss.hasPermission('bpm:task:query')")
    public CommonResult<List<BpmModifyRequestRespVO>> getPendingModifyRequestListByTaskId(@RequestParam("taskId") String taskId) {
        List<BpmModifyRequestDO> list = modifyRequestService.getPendingModifyRequestListByTaskId(taskId);
        return success(convertList(list, item -> BeanUtils.toBean(item, BpmModifyRequestRespVO.class)));
    }

    @GetMapping("/list-by-process-instance")
    @Operation(summary = "查询流程实例的修改申请")
    @Parameter(name = "processInstanceId", description = "流程实例 ID", required = true)
    @PreAuthorize("@ss.hasPermission('bpm:task:query')")
    public CommonResult<List<BpmModifyRequestRespVO>> getModifyRequestListByProcessInstanceId(
            @RequestParam("processInstanceId") String processInstanceId) {
        List<BpmModifyRequestDO> list = modifyRequestService.getModifyRequestListByProcessInstanceId(processInstanceId);
        return success(convertList(list, item -> BeanUtils.toBean(item, BpmModifyRequestRespVO.class)));
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "接受修改申请")
    @PreAuthorize("@ss.hasPermission('bpm:task:update')")
    public CommonResult<Boolean> acceptModifyRequest(@PathVariable("id") Long id,
                                                     @Valid @RequestBody BpmModifyRequestHandleReqVO reqVO) {
        modifyRequestService.acceptModifyRequest(getLoginUserId(), id, reqVO);
        return success(true);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "拒绝修改申请")
    @PreAuthorize("@ss.hasPermission('bpm:task:update')")
    public CommonResult<Boolean> rejectModifyRequest(@PathVariable("id") Long id,
                                                     @Valid @RequestBody BpmModifyRequestHandleReqVO reqVO) {
        modifyRequestService.rejectModifyRequest(getLoginUserId(), id, reqVO);
        return success(true);
    }

}

package com.clou.merchantdemo.controller;

import com.clou.common.result.Result;
import com.clou.merchantdemo.POJO.vo.PageVO;
import com.clou.merchantdemo.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/merchant/application")
@Slf4j
public class MerchantApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/comment/delete")
    public Result<Void> applyDeleteComment(@RequestParam("commentId") String commentId,
                                           @RequestParam(value = "reason", required = false) String reason,
                                           @RequestParam(value = "phone", required = false) String phone,
                                           @RequestHeader("id") Long id) {
        log.info("商家申请删除评论: merchantId={}, commentId={}", id, commentId);
        return applicationService.applyDeleteComment(commentId, reason, phone, id);
    }

    @PostMapping("/product/restore")
    public Result<Void> applyRestoreProduct(@RequestParam("productId") Long productId,
                                            @RequestParam(value = "reason", required = false) String reason,
                                            @RequestParam(value = "phone", required = false) String phone,
                                            @RequestHeader("id") Long id) {
        log.info("商家申请恢复商品: merchantId={}, productId={}", id, productId);
        return applicationService.applyRestoreProduct(productId, reason, phone, id);
    }

    @GetMapping("/list")
    public Result<PageVO> getApplicationList(@RequestHeader("id") Long id,
                                             @RequestParam(value = "requestType", required = false) Integer requestType,
                                             @RequestParam(value = "requestStatus", required = false) Integer requestStatus,
                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("查询商家申请列表: merchantId={}, requestType={}, requestStatus={}, page={}, size={}",
                id, requestType, requestStatus, page, size);
        return applicationService.getApplicationList(id, requestType, requestStatus, page, size);
    }

    @GetMapping("/refund/list")
    public Result<PageVO> getRefundApplicationList(@RequestHeader("id") Long id,
                                                   @RequestParam(value = "orderId", required = false) String orderId,
                                                   @RequestParam(value = "userId", required = false) Long userId,
                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("查询退款申请列表: merchantId={}, orderId={}, userId={}, page={}, size={}",
                id, orderId, userId, page, size);
        return applicationService.getRefundApplicationList(id, orderId, userId, page, size);
    }

    @PostMapping("/refund/process")
    public Result<Void> processRefundApplication(@RequestBody Map<String, Object> params,
                                                 @RequestHeader("id") Long id) {
        Long applicationId = Long.valueOf(params.get("applicationId").toString());
        Integer operationType = Integer.valueOf(params.get("operationType").toString());
        String feedback = params.get("feedback") != null ? params.get("feedback").toString() : null;
        log.info("处理退款申请: merchantId={}, applicationId={}, operationType={}", id, applicationId, operationType);
        return applicationService.processRefundApplication(applicationId, operationType, feedback, id);
    }

}

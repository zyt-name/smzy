package com.clou.userdemo.controller;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.ApplicationDTO;
import com.clou.userdemo.POJO.vo.PageVO;
import com.clou.userdemo.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/application")
@Slf4j

public class UserApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/refund")
    public Result<Void> applyRefund(@RequestBody ApplicationDTO applicationDTO,
                                    @RequestHeader("id") Long id) {
        log.info("用户申请退款: userId={}, orderId={}", id, applicationDTO.getTargetId());
        return applicationService.applyRefund(applicationDTO, id);
    }

    @PostMapping("/report/merchant")
    public Result<Void> reportMerchant(@RequestBody ApplicationDTO applicationDTO,
                                       @RequestHeader("id") Long id) {
        log.info("用户举报商家: userId={}, merchantId={}", id, applicationDTO.getTargetId());
        return applicationService.reportMerchant(applicationDTO, id);
    }

    @PostMapping("/report/product")
    public Result<Void> reportProduct(@RequestBody ApplicationDTO applicationDTO,
                                      @RequestHeader("id") Long id) {
        log.info("用户举报商品: userId={}, productId={}", id, applicationDTO.getTargetId());
        return applicationService.reportProduct(applicationDTO, id);
    }

    @GetMapping("/list")
    public Result<PageVO> getApplicationList(@RequestHeader("id") Long id,
                                             @RequestParam(value = "requestType", required = false) Integer requestType,
                                             @RequestParam(value = "requestStatus", required = false) Integer requestStatus,
                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("查询用户申请列表: userId={}, requestType={}, requestStatus={}, page={}, size={}",
                id, requestType, requestStatus, page, size);
        return applicationService.getApplicationList(id, requestType, requestStatus, page, size);
    }

}

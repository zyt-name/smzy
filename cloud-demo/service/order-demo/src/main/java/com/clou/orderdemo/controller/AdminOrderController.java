package com.clou.orderdemo.controller;

import com.clou.common.result.Result;
import com.clou.orderdemo.POJO.po.OrderItemsPO;
import com.clou.orderdemo.POJO.po.OrderPO;
import com.clou.orderdemo.service.AdminOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order/admin")
@Slf4j
public class AdminOrderController {

    @Autowired
    AdminOrderService adminOrderService;

    /**
     * 根据订单号查询订单信息
     * @param orderNo
     * @return
     */
    @GetMapping("/byOrderNo")
    public Result<OrderPO> byOrderNo(@RequestParam(value = "orderNo") String orderNo) {
        log.info("管理员根据订单号查询订单信息{}", orderNo);
        return Result.success(adminOrderService.byOrderNo(orderNo));
    }

    /**
     * 根据订单号查询订单详情
     * @param orderId
     * @return
     */
    @GetMapping("/byIdItems/{orderId}")
    public Result<List<OrderItemsPO>> byId(@PathVariable Long orderId){
        log.info("管理员根据订单号查询订单详情{}", orderId);
        return Result.success(adminOrderService.byId(orderId));
    }

    /**
     * 订单退款
     * @param orderId 订单ID
     * @return
     */
    @PostMapping("/refund/{orderId}")
    public Result<Void> refundOrder(@PathVariable Long orderId) {
        log.info("管理员退款订单, orderId={}", orderId);
        adminOrderService.refundOrder(orderId);
        return Result.success();
    }

}

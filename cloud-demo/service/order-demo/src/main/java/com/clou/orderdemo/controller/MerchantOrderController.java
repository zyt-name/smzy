package com.clou.orderdemo.controller;

import com.clou.common.result.Result;
import com.clou.orderdemo.POJO.po.OrderItemsPO;
import com.clou.orderdemo.POJO.vo.OrderVO;
import com.clou.orderdemo.POJO.vo.PageVO;
import com.clou.orderdemo.service.MerchantOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LENOVO
 */
@RestController
@RequestMapping("/order/merchant")
@Slf4j
public class MerchantOrderController {

    @Autowired
    MerchantOrderService merchantOrderService;

    /**
     * 商户查看自己的订单列表（合并接口）
     * 支持：查全部、按状态筛选、按订单号查询
     * @param merchantId
     * @param status
     * @param orderNo
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public Result<PageVO<OrderVO>> listOrders(@RequestHeader("id") Long merchantId,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String orderNo,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("商家:{} 查看订单列表, 状态:{}, 订单号:{}, 页码:{}, 每页大小:{}", merchantId, status, orderNo, page, pageSize);
        return Result.success(merchantOrderService.listOrders(merchantId, status, orderNo, page, pageSize));
    }

    /**
     * 根据订单号查询订单详情
     * @param orderId
     * @return
     */
    @GetMapping("/byId/{orderId}")
    public Result<List<OrderItemsPO>> byId(@PathVariable Long orderId) throws JsonProcessingException {
        log.info("根据订单号查询订单详情:{}", orderId);
        return Result.success(merchantOrderService.byId(orderId));
    }

    /**
     * 取消订单
     * @param orderId
     * @param reason
     * @return
     */
    @PutMapping("/cancel/{orderId}")
    public Result cancel(@PathVariable Long orderId,
                         @RequestParam("cancelReason") String reason) {
        log.info("取消订单:{} 原因:{}", orderId, reason);
        merchantOrderService.cancel(orderId, reason);
        return Result.success();
    }

    /**
     * 商家发货
     * @param orderId
     * @return
     */
    @PutMapping("/confirm/{orderId}")
    public Result confirm(@PathVariable Long orderId) {
        log.info("订单:{} 发货", orderId);
        merchantOrderService.confirm(orderId);
        return Result.success();
    }

    /**
     * 商家逻辑删除订单（修改merchant_delete字段为1）
     * @param orderId
     * @return
     */
    @PutMapping("/delete/{orderId}")
    public Result delete(@PathVariable Long orderId) {
        log.info("商家逻辑删除订单:{}", orderId);
        merchantOrderService.delete(orderId);
        return Result.success();
    }

}

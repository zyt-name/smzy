package com.clou.orderdemo.controller;

import com.clou.apidemo.POJO.vo.UserCartVO;
import com.clou.apidemo.client.UserClient;
import com.clou.common.result.Result;
import com.clou.orderdemo.POJO.dto.PayOrderDTO;
import com.clou.orderdemo.POJO.vo.OrderItemsVO;
import com.clou.orderdemo.POJO.vo.OrderVO;
import com.clou.orderdemo.service.UserOrderService;
import com.clou.servicecommon.annotation.UserOperationLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LENOVO
 */
@RestController
@RequestMapping("/order/user")
@Slf4j
public class UserOrderController {

    @Autowired
    UserOrderService userOrderService;
    @Autowired
    UserClient userClient;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 结算购物车
     * @param userId
     * @param addressId
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/cartToOrder")
    @UserOperationLog(operateType = 3)
    public Result CartToOrder(@RequestHeader("id")  Long userId,
                              @RequestParam("addressId")  Long addressId,
                              @RequestParam("remark")  String remark  ) throws JsonProcessingException {
        log.info("结算购物车 用户id:{}, 用户地址id:{}", userId, addressId);
        // 根据用户id查询购物车信息
        List<UserCartVO> userCarts = userClient.showCartClient(userId);
        log.info("购物车信息:{}", userCarts);
        // 根据用户地址id查询用户地址信息并转为json字符串
        String userAddress = objectMapper.writeValueAsString(userClient.getUserAddressById(addressId));
        log.info("用户地址信息:{}", userAddress);
        // 调用服务层添加订单
        Long orderId = userOrderService.addOrder(userCarts, userAddress,remark);
        return Result.success(orderId);
    }

    /**
     * 下单单个商品
     * @param userId 用户id
     * @param productId 商品id
     * @param quantity 购买数量
     * @param addressId 地址id
     * @param remark 备注
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/buySingleItem")
    @UserOperationLog(operateType = 3)
    public Result buySingleItem(@RequestHeader("id") Long userId,
                                @RequestParam("productId") Long productId,
                                @RequestParam("quantity") Integer quantity,
                                @RequestParam("addressId") Long addressId,
                                @RequestParam("remark") String remark) throws JsonProcessingException {
        log.info("下单单个商品 用户id:{}, 商品id:{}, 数量:{}, 地址id:{}", userId, productId, quantity, addressId);
        // 根据用户地址id查询用户地址信息并转为json字符串
        String userAddress = objectMapper.writeValueAsString(userClient.getUserAddressById(addressId));
        log.info("用户地址信息:{}", userAddress);
        // 调用服务层添加单个商品订单
        Long orderId = userOrderService.addSingleOrder(userId, productId, quantity, userAddress, remark);
        return Result.success(orderId);
    }

    /**
     * 用户逻辑删除订单（修改user_delete字段为1）
     * @param orderId
     * @return
     */
    @PutMapping("/deleteOrder/{orderId}")
    public Result deleteOrder(@PathVariable Long orderId) {
        log.info("用户逻辑删除订单 id:{}", orderId);
        userOrderService.deleteOrder(orderId);
        return Result.success();
    }

    /**
     * 获取订单列表
     * @param userId
     * @return
     */
    @GetMapping("/listOrder")
    public Result<List<OrderVO>> listOrder(@RequestHeader("id") Long userId) {
        log.info("获取订单列表 用户id:{}", userId);
        List<OrderVO> orderList = userOrderService.getOrderList(userId);
        return Result.success(orderList);
    }

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    @GetMapping("/orderDetail/{orderId}")
    public Result<List<OrderItemsVO>> showCartClient(@PathVariable Long orderId) {
        log.info("获取订单详情 id:{}", orderId);
        List<OrderItemsVO> orderItemsList = userOrderService.getOrderItems(orderId);
        return Result.success(orderItemsList);
    }

    /**
     * 根据订单号查询订单
     * @param orderNo
     * @return
     */
    @GetMapping("/orderDetailByOrderNo")
    public Result<OrderVO> byOrderNo(@RequestParam("orderNo") String orderNo) {
        log.info("根据订单号查询订单:{}", orderNo);
        OrderVO order = userOrderService.byOrderNo(orderNo);
        return Result.success(order);
    }

    /**
     * 根据订单ID查询订单信息（用于支付页面）
     * @param orderId
     * @return
     */
    @GetMapping("/getOrderById/{orderId}")
    public Result<OrderVO> getOrderById(@PathVariable Long orderId) {
        log.info("根据订单ID查询订单:{}", orderId);
        OrderVO order = userOrderService.getOrderById(orderId);
        return Result.success(order);
    }

    /**
     * 模拟支付订单
     * @param payOrderDTO
     * @return
     */
    @PutMapping("/payOrder")
    @UserOperationLog(operateType = 5)
    public Result payOrder(@RequestBody PayOrderDTO payOrderDTO) {
        log.info("支付订单 {}", payOrderDTO);
        userOrderService.pay(payOrderDTO);
        return Result.success();
    }

    /**
     * 用户手动取消订单
     * @param orderId
     * @return
     */
    @PutMapping("/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId,
                              @RequestParam("cancelReason") String cancelReason) {
        log.info("取消订单 id:{}", orderId);
        userOrderService.cancel(orderId,cancelReason);
        return Result.success();
    }

    /**
     * 确认收货
     * @param orderId
     * @return
     */
    @PutMapping("/confirmReceive/{orderId}")
    @UserOperationLog(operateType = 6)
    public Result confirmReceive(@PathVariable Long orderId) {
        log.info("确认收货 id:{}", orderId);
        userOrderService.confirmReceive(orderId);
        return Result.success();
    }

}

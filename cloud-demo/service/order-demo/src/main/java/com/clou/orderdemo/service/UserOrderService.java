package com.clou.orderdemo.service;

import com.clou.apidemo.POJO.vo.UserCartVO;
import com.clou.orderdemo.POJO.dto.PayOrderDTO;
import com.clou.orderdemo.POJO.po.OrderPO;
import com.clou.orderdemo.POJO.vo.OrderItemsVO;
import com.clou.orderdemo.POJO.vo.OrderVO;

import java.util.List;

public interface UserOrderService {

    Long addOrder(List<UserCartVO> userCarts,String userAddress,String remark);

    /**
     * 添加单个商品订单
     * @param userId 用户id
     * @param productId 商品id
     * @param quantity 购买数量
     * @param userAddress 用户地址
     * @param remark 备注
     * @return 订单ID
     */
    Long addSingleOrder(Long userId, Long productId, Integer quantity, String userAddress, String remark);

    // 用户逻辑删除订单（修改user_delete字段）
    void deleteOrder(Long orderId);

    // 查看用户对应的所有订单
    List<OrderVO> getOrderList(Long userId);

    // 查看对应订单的订单详情
    List<OrderItemsVO> getOrderItems(Long orderId);

    // 根据订单号查询订单
    OrderVO byOrderNo(String orderNo);

    // 根据订单ID查询订单信息（用于支付页面）
    OrderVO getOrderById(Long orderId);

    // 更改订单状态
    void updateOrderStatus(Long orderId, int status);

    OrderPO byId(Long id);

    // 支付订单
    void pay(PayOrderDTO payOrderDTO);

    // 订单取消
    void cancel(Long orderId,String cancelReason);

    // 确认收货
    void confirmReceive(Long orderId);
}

package com.clou.orderdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsVO {

    private Long id;
    // 订单id
    private Long orderId;
    // 商品id
    private Long productId;
    // 商品名称
    private String productName;
    // 商品单价
    private float price;
    // 商品数量
    private int quantity;
    // 商品总价
    private float subtotal;
    // 商家id
    private Long merchantId;
    // 用户id
    private Long userId;
    // 用户地址
    private String userAddress;
    // 订单号
    private String orderNo;
    // 取消原因
    private String cancelReason;

}

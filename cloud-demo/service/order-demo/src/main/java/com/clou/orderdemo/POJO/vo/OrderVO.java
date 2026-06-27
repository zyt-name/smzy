package com.clou.orderdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {

    private Long id;
    // 订单号
    private String orderNo;
    // 用户id
    private Long userId;
    // 订单总价格
    private float totalPrice;
    // 订单状态
    private int status;
    // 支付方式
    private String paymentMethod;
    // 订单取消原因
    private String cancelReason;
    // 订单备注
    private String remark;
    // 创建时间
    private LocalDateTime createdAt;
    // 收货地址信息（从user_address解析）
    private Map<String, Object> shippingAddress;
    // 收货人信息（从user_address解析）
    private Map<String, Object> shippingInformation;

}

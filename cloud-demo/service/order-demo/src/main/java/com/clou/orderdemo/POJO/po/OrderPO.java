package com.clou.orderdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPO {

    private Long id;
    // 订单编号
    private String orderNo;
    // 用户ID
    private Long userId;
    // 订单状态（0-待支付，1-已支付，2-已发货，3-已完成，4-已取消）
    private Integer status;
    // 订单总金额
    private float totalPrice;
    // 支付方式
    private String paymentMethod;
    // 创建时间
    private LocalDateTime createdAt;
    // 更新时间
    private LocalDateTime updatedAt;
    // 订单取消原因
    private String cancelReason;
    // 订单备注
    private String remark;
    // 用户方删除标记（0未删除，1删除）
    private Integer userDelete;
    // 商家方删除标记（0未删除，1删除）
    private Integer merchantDelete;

}

package com.clou.common.mqPO;

import lombok.Data;

@Data
public class orderCartMessage {

    // 必须：需要清空购物车的用户ID
    private Long userId;
    // 可选：关联的订单号，用于日志追踪
    private String orderNo;
}
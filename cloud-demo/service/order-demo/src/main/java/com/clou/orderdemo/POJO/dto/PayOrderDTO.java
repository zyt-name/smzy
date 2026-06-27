package com.clou.orderdemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderDTO {

    private Long id;
    // 支付方式
    private String paymentMethod;

}

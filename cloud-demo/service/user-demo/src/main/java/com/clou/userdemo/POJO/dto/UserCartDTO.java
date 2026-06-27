package com.clou.userdemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCartDTO {


    // 商家id
    private Long merchantId;
    // 商品id
    private Long productId;
    // 选取的商品数量
    private Long productCount;
    // 用户选取的商品规格
    private String userSpecification;

}

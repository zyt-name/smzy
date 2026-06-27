package com.clou.apidemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCartVO {

    // 用户id
    private Long userId;
    // 商品id
    private Long productId;
    // 商户id
    private Long merchantId;
    // 选取的商品数量
    private Long productCount;
    // 商品名称
    private String productName;
    // 商品单价
    private float price;
    // 商品图片
    private String imageUrl;
    // 用户需要的商品规格
    private String userSpecification;

}

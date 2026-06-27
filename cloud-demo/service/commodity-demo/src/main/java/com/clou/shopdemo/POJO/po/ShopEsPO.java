package com.clou.shopdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopEsPO {

    private String name;
    private float price;
    private String category;
    private String imageUrls;
    // 0上架，1下架
    private int status;
    // 库存数量
    private Long stock;
    // 商家id
    private Long merchantId;
    // 管理员管理商品状态（0-正常，1-强制下架）
    private Integer adminStatus;

}

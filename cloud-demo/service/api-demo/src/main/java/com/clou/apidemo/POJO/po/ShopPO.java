package com.clou.apidemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopPO {

    private Long id;
    // 商品名称
    private String name;
    // 价格
    private float price;
    // 商品存货
    private Long stock;
    // 商家id
    private Long merchantId;
    // 商品状态，0为上架，1下架
    private int status;
    // 商品分类
    private String category;
    private LocalDateTime createdAt;
    // 商品图片
    private String imageUrls;
    // 管理员管理商品状态（0-正常，1-强制下架）
    private Integer adminStatus;
    // 商品标签
    private String label;

}

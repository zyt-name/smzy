package com.clou.shopdemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {

    private String name;
    private float price;
    // 商品存货数量
    private Long stock;
    // 商品分类
    private String category;
    // 商品描述
    private String description;
    // 商品规格
    private String specification;
    // 商品图片
    private String imageUrls;
    // 商品标签
    private String label;

}

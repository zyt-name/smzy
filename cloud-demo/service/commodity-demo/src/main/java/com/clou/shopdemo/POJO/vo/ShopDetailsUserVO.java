package com.clou.shopdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDetailsUserVO {

    // 商品id
    private Long shopId;
    // 商品描述
    private String description;
    // 商品规格
    private String specification;
    // 商品图片
    private String imageUrls;
    // 商品名称
    private String name;
    // 商品分类
    private String category;
    // 商品标签
    private List<String> label;
    // 商品价格
    private float price;

}

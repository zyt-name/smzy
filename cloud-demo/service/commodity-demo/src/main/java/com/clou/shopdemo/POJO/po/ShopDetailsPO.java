package com.clou.shopdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDetailsPO {

    private Long id;
    // 商品id
    private Long shopId;
    // 商品描述
    private String description;
    // 商品规格
    private String specification;
    // 商品图片
    private String imageUrls;
    // 商品标签
    private String label;
    // 创建时间
    private LocalDateTime updatedAt;

}

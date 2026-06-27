package com.clou.apidemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopEsVO {

    private Long id;
    private String name;
    private float price;
    private String category;
    private String imageUrls;
    private Long stock;
    // 商家id
    private Long merchantId;
    // 商品标签
    private List<String> label;
    // 商品状态，0为上架，1为下架
    private Integer status;
    // 管理员管理商品状态（0-正常，1-强制下架）
    private Integer adminStatus;

}

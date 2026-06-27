package com.clou.apidemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminShopSearchDTO {

    // 商品名称（模糊查询）
    private String name;
    // 商品分类
    private String category;
    // 商家ID
    private Long merchantId;
    // 页码，默认第1页
    private Integer pageNum = 1;
    // 每页条数，默认12条
    private Integer pageSize = 12;

}

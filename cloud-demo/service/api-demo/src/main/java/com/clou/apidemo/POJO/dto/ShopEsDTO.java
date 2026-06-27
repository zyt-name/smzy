package com.clou.apidemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopEsDTO {

    private String name;
    // 最小价格
    private Float minPrice;
    // 最大价格
    private Float maxPrice;
    // 分类（支持多个分类，使用OR查询）
    private List<String> categories;
    // 商家ID
    private Long merchantId;
    // 页码，默认第1页
    private Integer pageNum = 1;
    // 每页条数，固定100条
    public static final int PAGE_SIZE = 100;

}

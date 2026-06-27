package com.clou.orderdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 交易构成统计VO
 * 按商品分类统计订单中的商品数量
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStructureVO {

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 交易构成数据
     * key: 商品分类名称
     * value: 对应分类的商品数量
     */
    private Map<String, Long> structureMap;
}

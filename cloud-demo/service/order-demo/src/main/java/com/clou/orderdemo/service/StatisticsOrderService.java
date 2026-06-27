package com.clou.orderdemo.service;

import com.clou.orderdemo.POJO.vo.OrderStatisticsVO;
import com.clou.orderdemo.POJO.vo.TransactionStructureVO;

public interface StatisticsOrderService {

    /**
     * 获取商家订单统计曲线图数据（近7天或近30天）
     * @param merchantId 商家ID
     * @param viewType 查看类型（1-近7天，2-近30天）
     * @return 订单统计VO
     */
    OrderStatisticsVO getOrderStatisticsCurve(Long merchantId, Integer viewType);

    /**
     * 获取商家交易构成统计数据（按商品分类统计）
     * @param merchantId 商家ID
     * @return 交易构成统计VO
     */
    TransactionStructureVO getTransactionStructure(Long merchantId);
}

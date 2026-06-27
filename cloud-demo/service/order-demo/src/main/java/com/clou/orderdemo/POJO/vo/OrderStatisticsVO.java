package com.clou.orderdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 订单统计VO（用于曲线图展示）
 * 数据格式为独立列表，方便前端ECharts直接使用
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatisticsVO {

    /**
     * 统计日期范围（近7天或近30天）
     */
    private Integer days;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 日期列表
     */
    private List<LocalDate> dates;

    /**
     * 订单数量列表（每天的订单数）
     */
    private List<Long> orderCounts;

    /**
     * 有效订单数量列表（每天的有效订单数，排除待支付、已取消、退单）
     */
    private List<Long> validOrderCounts;

    /**
     * 交易额列表（每天的交易额）
     */
    private List<BigDecimal> amounts;

    /**
     * 有效交易额列表（每天的有效交易额，排除待支付、已取消、退单）
     */
    private List<BigDecimal> validAmounts;

    /**
     * 订单总数（统计范围内）
     */
    private Long totalOrderCount;

    /**
     * 有效订单总数（统计范围内）
     */
    private Long validOrderCount;

    /**
     * 交易总额（统计范围内）
     */
    private BigDecimal totalAmount;

    /**
     * 有效交易总额（统计范围内，排除待支付、已取消、退单）
     */
    private BigDecimal validAmount;
}

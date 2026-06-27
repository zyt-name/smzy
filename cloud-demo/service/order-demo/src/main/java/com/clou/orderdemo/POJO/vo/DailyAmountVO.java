package com.clou.orderdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日交易额统计VO（Mapper返回的原始数据）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAmountVO {

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 交易额
     */
    private BigDecimal amount;
}

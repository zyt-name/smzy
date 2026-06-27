package com.clou.orderdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每日订单数量统计VO（Mapper返回的原始数据）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyOrderCountVO {

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 订单数量
     */
    private Long orderCount;
}

package com.clou.orderdemo.service.impl;

import com.clou.orderdemo.POJO.vo.DailyAmountVO;
import com.clou.orderdemo.POJO.vo.DailyOrderCountVO;
import com.clou.orderdemo.POJO.vo.OrderStatisticsVO;
import com.clou.orderdemo.POJO.vo.TransactionStructureVO;
import com.clou.orderdemo.mapper.OrderItemsMapper;
import com.clou.orderdemo.mapper.OrderMapper;
import com.clou.orderdemo.service.StatisticsOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticsOrderServiceImpl implements StatisticsOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Override
    public OrderStatisticsVO getOrderStatisticsCurve(Long merchantId, Integer viewType) {
        log.info("获取商家:{} 的订单统计数据，查看类型: {}", merchantId, viewType);

        // 确定统计天数（1-近7天，2-近30天）
        int days = (viewType != null && viewType == 2) ? 30 : 7;

        // 计算日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 先在订单详情表中查询该商家的所有订单ID
        List<Long> orderIds = orderItemsMapper.selectOrderIdsByMerchantId(merchantId);

        // 如果没有订单数据，返回空的统计结果
        if (orderIds == null || orderIds.isEmpty()) {
            log.info("商家:{} 没有订单数据", merchantId);
            return buildEmptyStatistics(merchantId, days, startDate, endDate);
        }

        // 查询每天的订单数量
        List<DailyOrderCountVO> dailyOrderCounts = orderMapper.selectDailyOrderCountByIds(orderIds, startDate, endDate);

        // 查询每天的有效订单数量（排除状态为0、4、5的订单）
        List<DailyOrderCountVO> dailyValidOrderCounts = orderMapper.selectDailyValidOrderCountByIds(orderIds, startDate, endDate);

        // 查询每天的交易额
        List<DailyAmountVO> dailyAmounts = orderMapper.selectDailyAmountByIds(orderIds, startDate, endDate);

        // 查询每天的有效交易额（排除状态为0、4、5的订单）
        List<DailyAmountVO> dailyValidAmounts = orderMapper.selectDailyValidAmountByIds(orderIds, startDate, endDate);

        // 将查询结果转换为Map，方便后续处理
        Map<LocalDate, Long> orderCountMap = new HashMap<>();
        for (DailyOrderCountVO vo : dailyOrderCounts) {
            orderCountMap.put(vo.getDate(), vo.getOrderCount());
        }

        Map<LocalDate, Long> validOrderCountMap = new HashMap<>();
        for (DailyOrderCountVO vo : dailyValidOrderCounts) {
            validOrderCountMap.put(vo.getDate(), vo.getOrderCount());
        }

        Map<LocalDate, BigDecimal> amountMap = new HashMap<>();
        for (DailyAmountVO vo : dailyAmounts) {
            amountMap.put(vo.getDate(), vo.getAmount() != null ? vo.getAmount() : BigDecimal.ZERO);
        }

        Map<LocalDate, BigDecimal> validAmountMap = new HashMap<>();
        for (DailyAmountVO vo : dailyValidAmounts) {
            validAmountMap.put(vo.getDate(), vo.getAmount() != null ? vo.getAmount() : BigDecimal.ZERO);
        }

        // 构建独立列表数据
        List<LocalDate> dates = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        List<Long> validOrderCounts = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<BigDecimal> validAmounts = new ArrayList<>();

        long totalOrderCount = 0;
        long totalValidOrderCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalValidAmount = BigDecimal.ZERO;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long orderCount = orderCountMap.getOrDefault(date, 0L);
            Long validOrderCount = validOrderCountMap.getOrDefault(date, 0L);
            BigDecimal amount = amountMap.getOrDefault(date, BigDecimal.ZERO);
            BigDecimal validAmount = validAmountMap.getOrDefault(date, BigDecimal.ZERO);

            dates.add(date);
            orderCounts.add(orderCount);
            validOrderCounts.add(validOrderCount);
            amounts.add(amount);
            validAmounts.add(validAmount);

            totalOrderCount += orderCount;
            totalValidOrderCount += validOrderCount;
            totalAmount = totalAmount.add(amount);
            totalValidAmount = totalValidAmount.add(validAmount);
        }

        // 构建返回结果
        OrderStatisticsVO statisticsVO = new OrderStatisticsVO();
        statisticsVO.setDays(days);
        statisticsVO.setMerchantId(merchantId);
        statisticsVO.setDates(dates);
        statisticsVO.setOrderCounts(orderCounts);
        statisticsVO.setValidOrderCounts(validOrderCounts);
        statisticsVO.setAmounts(amounts);
        statisticsVO.setValidAmounts(validAmounts);
        statisticsVO.setTotalOrderCount(totalOrderCount);
        statisticsVO.setValidOrderCount(totalValidOrderCount);
        statisticsVO.setTotalAmount(totalAmount);
        statisticsVO.setValidAmount(totalValidAmount);

        log.info("商家:{} 的订单统计完成，总订单数:{}, 有效订单数:{}, 总交易额:{}, 有效交易额:{}",
                merchantId, totalOrderCount, totalValidOrderCount, totalAmount, totalValidAmount);
        return statisticsVO;
    }

    /**
     * 构建空的统计结果（当商家没有订单数据时）
     */
    private OrderStatisticsVO buildEmptyStatistics(Long merchantId, int days, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        List<Long> validOrderCounts = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<BigDecimal> validAmounts = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date);
            orderCounts.add(0L);
            validOrderCounts.add(0L);
            amounts.add(BigDecimal.ZERO);
            validAmounts.add(BigDecimal.ZERO);
        }

        OrderStatisticsVO statisticsVO = new OrderStatisticsVO();
        statisticsVO.setDays(days);
        statisticsVO.setMerchantId(merchantId);
        statisticsVO.setDates(dates);
        statisticsVO.setOrderCounts(orderCounts);
        statisticsVO.setValidOrderCounts(validOrderCounts);
        statisticsVO.setAmounts(amounts);
        statisticsVO.setValidAmounts(validAmounts);
        statisticsVO.setTotalOrderCount(0L);
        statisticsVO.setValidOrderCount(0L);
        statisticsVO.setTotalAmount(BigDecimal.ZERO);
        statisticsVO.setValidAmount(BigDecimal.ZERO);

        return statisticsVO;
    }

    @Override
    public TransactionStructureVO getTransactionStructure(Long merchantId) {
        log.info("获取商家:{} 的交易构成统计数据", merchantId);

        // 查询交易构成统计数据
        List<Map<String, Object>> structureList = orderMapper.selectTransactionStructureByMerchantId(merchantId);

        // 转换为Map结构
        Map<String, Long> structureMap = new HashMap<>();
        if (structureList != null && !structureList.isEmpty()) {
            for (Map<String, Object> item : structureList) {
                String category = (String) item.get("category");
                Long count = ((Number) item.get("count")).longValue();
                structureMap.put(category, count);
            }
        }

        // 构建返回结果
        TransactionStructureVO structureVO = new TransactionStructureVO();
        structureVO.setMerchantId(merchantId);
        structureVO.setStructureMap(structureMap);

        log.info("商家:{} 的交易构成统计完成，共{}个分类", merchantId, structureMap.size());
        return structureVO;
    }
}

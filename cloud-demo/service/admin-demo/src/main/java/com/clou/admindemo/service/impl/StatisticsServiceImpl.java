package com.clou.admindemo.service.impl;

import com.clou.admindemo.POJO.vo.MerchantStatisticsVO;
import com.clou.admindemo.POJO.vo.PlatformOverviewVO;
import com.clou.admindemo.POJO.vo.UserStatisticsVO;
import com.clou.admindemo.mapper.StatisticsMapper;
import com.clou.admindemo.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public UserStatisticsVO getUserStatistics(Integer type) {
        log.info("获取用户统计数据, type: {}", type);

        int days = (type != null && type == 2) ? 30 : 7;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<String> dateList = generateDateList(startDate, endDate);
        Map<String, Integer> newUserMap = getCountMap(statisticsMapper.countNewUsersByDateRange(startDate, endDate));
        Map<String, Integer> activeUserMap = getCountMap(statisticsMapper.countActiveUsersByDateRange(startDate, endDate));

        List<Integer> newUserCounts = new ArrayList<>();
        List<Integer> activeUserCounts = new ArrayList<>();

        for (String date : dateList) {
            newUserCounts.add(newUserMap.getOrDefault(date, 0));
            activeUserCounts.add(activeUserMap.getOrDefault(date, 0));
        }

        UserStatisticsVO vo = new UserStatisticsVO();
        vo.setDates(dateList);
        vo.setNewUserCounts(newUserCounts);
        vo.setActiveUserCounts(activeUserCounts);

        return vo;
    }

    @Override
    public MerchantStatisticsVO getMerchantStatistics(Integer type) {
        log.info("获取商家统计数据, type: {}", type);

        int days = (type != null && type == 2) ? 30 : 7;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<String> dateList = generateDateList(startDate, endDate);
        Map<String, Integer> newMerchantMap = getCountMap(statisticsMapper.countNewMerchantsByDateRange(startDate, endDate));
        Map<String, Integer> activeMerchantMap = getCountMap(statisticsMapper.countActiveMerchantsByDateRange(startDate, endDate));

        List<Integer> newMerchantCounts = new ArrayList<>();
        List<Integer> activeMerchantCounts = new ArrayList<>();

        for (String date : dateList) {
            newMerchantCounts.add(newMerchantMap.getOrDefault(date, 0));
            activeMerchantCounts.add(activeMerchantMap.getOrDefault(date, 0));
        }

        MerchantStatisticsVO vo = new MerchantStatisticsVO();
        vo.setDates(dateList);
        vo.setNewMerchantCounts(newMerchantCounts);
        vo.setActiveMerchantCounts(activeMerchantCounts);

        return vo;
    }

    /**
     * 生成日期列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期字符串列表（格式：yyyy-MM-dd）
     */
    private List<String> generateDateList(LocalDate startDate, LocalDate endDate) {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateList.add(current.format(formatter));
            current = current.plusDays(1);
        }
        return dateList;
    }

    @Override
    public PlatformOverviewVO getPlatformOverview() {
        log.info("获取平台概览统计数据");

        PlatformOverviewVO vo = new PlatformOverviewVO();
        vo.setTotalUsers(statisticsMapper.countTotalUsers());
        vo.setTotalMerchants(statisticsMapper.countTotalMerchants());
        vo.setTotalProducts(statisticsMapper.countTotalProducts());
        vo.setTotalOrders(statisticsMapper.countTotalOrders());
        vo.setCompletedOrders(statisticsMapper.countCompletedOrders());
        vo.setBannedUsers(statisticsMapper.countBannedUsers());
        vo.setBannedMerchants(statisticsMapper.countBannedMerchants());

        return vo;
    }

    /**
     * 将查询结果转换为日期-数量的Map
     * @param resultList 查询结果列表
     * @return 日期和数量的映射
     */
    private Map<String, Integer> getCountMap(List<Map<String, Object>> resultList) {
        Map<String, Integer> countMap = new HashMap<>();
        if (resultList != null) {
            for (Map<String, Object> result : resultList) {
                Object dateObj = result.get("date");
                Object countObj = result.get("count");
                if (dateObj != null && countObj != null) {
                    String date = dateObj.toString();
                    Integer count = ((Number) countObj).intValue();
                    countMap.put(date, count);
                }
            }
        }
        return countMap;
    }
}

package com.clou.admindemo.controller;

import com.clou.admindemo.POJO.vo.MerchantStatisticsVO;
import com.clou.admindemo.POJO.vo.PlatformOverviewVO;
import com.clou.admindemo.POJO.vo.UserStatisticsVO;
import com.clou.admindemo.service.StatisticsService;
import com.clou.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取用户统计数据（新增用户曲线和用户活跃度曲线）
     * @param type 查询类型：1=近7天，2=近30天
     * @return 用户统计数据
     */
    @GetMapping("/user")
    public Result<UserStatisticsVO> getUserStatistics(@RequestParam Integer type) {
        log.info("获取用户统计数据, type: {}", type);
        return Result.success(statisticsService.getUserStatistics(type));
    }

    /**
     * 获取商家统计数据（新增商家曲线和商家活跃度曲线）
     * @param type 查询类型：1=近7天，2=近30天
     * @return 商家统计数据
     */
    @GetMapping("/merchant")
    public Result<MerchantStatisticsVO> getMerchantStatistics(@RequestParam Integer type) {
        log.info("获取商家统计数据, type: {}", type);
        return Result.success(statisticsService.getMerchantStatistics(type));
    }

    /**
     * 获取平台概览统计数据
     * @return 平台概览统计数据
     */
    @GetMapping("/overview")
    public Result<PlatformOverviewVO> getPlatformOverview() {
        log.info("获取平台概览统计数据");
        return Result.success(statisticsService.getPlatformOverview());
    }
}

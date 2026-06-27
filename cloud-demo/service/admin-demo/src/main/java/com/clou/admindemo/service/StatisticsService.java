package com.clou.admindemo.service;

import com.clou.admindemo.POJO.vo.MerchantStatisticsVO;
import com.clou.admindemo.POJO.vo.PlatformOverviewVO;
import com.clou.admindemo.POJO.vo.UserStatisticsVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取用户统计数据（新增用户曲线和用户活跃度曲线）
     * @param type 查询类型：1=近7天，2=近30天
     * @return 用户统计数据
     */
    UserStatisticsVO getUserStatistics(Integer type);

    /**
     * 获取商家统计数据（新增商家曲线和商家活跃度曲线）
     * @param type 查询类型：1=近7天，2=近30天
     * @return 商家统计数据
     */
    MerchantStatisticsVO getMerchantStatistics(Integer type);

    /**
     * 获取平台概览统计数据
     * @return 平台概览统计数据
     */
    PlatformOverviewVO getPlatformOverview();
}

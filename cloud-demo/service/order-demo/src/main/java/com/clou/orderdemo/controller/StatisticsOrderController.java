package com.clou.orderdemo.controller;

import com.clou.common.result.Result;
import com.clou.orderdemo.POJO.vo.OrderStatisticsVO;
import com.clou.orderdemo.POJO.vo.TransactionStructureVO;
import com.clou.orderdemo.service.StatisticsOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单统计Controller
 * @author LENOVO
 */
@RestController
@RequestMapping("/order/statistics")
@Slf4j
public class StatisticsOrderController {

    @Autowired
    private StatisticsOrderService statisticsOrderService;

    /**
     * 获取商家订单统计曲线图数据（近7天或近30天）
     * @param userId 商家ID（从请求头获取，后端token解析后自动放入）
     * @param viewType 查看类型（1-近7天，2-近30天）
     * @return 订单统计VO，包含每天的订单数和有效订单数
     */
    @GetMapping("/curve")
    public Result<OrderStatisticsVO> getOrderStatisticsCurve(
            @RequestHeader("id") Long userId,
            @RequestParam("viewType") Integer viewType) {
        log.info("获取订单统计曲线图数据，商家ID:{}, 查看类型:{}", userId, viewType);

        // 参数校验
        if (viewType == null || (viewType != 1 && viewType != 2)) {
            log.warn("查看类型参数错误: {}", viewType);
            return Result.error("查看类型参数错误，只能是1（近7天）或2（近30天）");
        }

        OrderStatisticsVO statisticsVO = statisticsOrderService.getOrderStatisticsCurve(userId, viewType);
        return Result.success(statisticsVO);
    }

    /**
     * 获取商家交易构成统计数据（按商品分类统计）
     * @param userId 商家ID（从请求头获取，后端token解析后自动放入）
     * @return 交易构成统计VO，包含各商品分类的数量
     */
    @GetMapping("/structure")
    public Result<TransactionStructureVO> getTransactionStructure(
            @RequestHeader("id") Long userId) {
        log.info("获取交易构成统计数据，商家ID:{}", userId);

        TransactionStructureVO structureVO = statisticsOrderService.getTransactionStructure(userId);
        return Result.success(structureVO);
    }
}

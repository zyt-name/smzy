package com.clou.admindemo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {

    /**
     * 统计指定日期范围内每天的新增用户数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期和数量的映射列表
     */
    @Select("SELECT DATE(created_at) as date, COUNT(*) as count " +
            "FROM users " +
            "WHERE DATE(created_at) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY DATE(created_at)")
    List<Map<String, Object>> countNewUsersByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定日期范围内每天的用户活跃数（登录次数）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期和数量的映射列表
     */
    @Select("SELECT DATE(operate_time) as date, COUNT(DISTINCT user_no) as count " +
            "FROM login_log " +
            "WHERE operation = '用户登录' " +
            "AND user_no LIKE '300%' " +
            "AND DATE(operate_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(operate_time) " +
            "ORDER BY DATE(operate_time)")
    List<Map<String, Object>> countActiveUsersByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定日期范围内每天的新增商家数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期和数量的映射列表
     */
    @Select("SELECT DATE(created_at) as date, COUNT(*) as count " +
            "FROM merchants " +
            "WHERE DATE(created_at) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY DATE(created_at)")
    List<Map<String, Object>> countNewMerchantsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定日期范围内每天的商家活跃数（登录次数）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期和数量的映射列表
     */
    @Select("SELECT DATE(operate_time) as date, COUNT(DISTINCT user_no) as count " +
            "FROM login_log " +
            "WHERE operation = '商户登录' " +
            "AND user_no LIKE '200%' " +
            "AND DATE(operate_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(operate_time) " +
            "ORDER BY DATE(operate_time)")
    List<Map<String, Object>> countActiveMerchantsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计平台用户总数
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    Long countTotalUsers();

    /**
     * 统计平台商家总数
     * @return 商家总数
     */
    @Select("SELECT COUNT(*) FROM merchants")
    Long countTotalMerchants();

    /**
     * 统计商品总数
     * @return 商品总数
     */
    @Select("SELECT COUNT(*) FROM products")
    Long countTotalProducts();

    /**
     * 统计订单总数
     * @return 订单总数
     */
    @Select("SELECT COUNT(*) FROM orders")
    Long countTotalOrders();

    /**
     * 统计已成交订单数（状态为1-已支付，2-已发货，3-已完成）
     * @return 成交订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status IN (1, 2, 3)")
    Long countCompletedOrders();

    /**
     * 统计封禁用户数（status为1）
     * @return 封禁用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = 1")
    Long countBannedUsers();

    /**
     * 统计封禁商家数（status为1）
     * @return 封禁商家数
     */
    @Select("SELECT COUNT(*) FROM merchants WHERE status = 1")
    Long countBannedMerchants();

    @Select("SELECT COUNT(*) FROM products WHERE id = #{goodsId}")
    int existsByGoodsId(@Param("goodsId") Long goodsId);
}

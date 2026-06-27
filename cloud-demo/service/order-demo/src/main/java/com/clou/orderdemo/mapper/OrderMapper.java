package com.clou.orderdemo.mapper;

import com.clou.orderdemo.POJO.dto.PayOrderDTO;
import com.clou.orderdemo.POJO.po.OrderPO;
import com.clou.orderdemo.POJO.vo.DailyAmountVO;
import com.clou.orderdemo.POJO.vo.DailyOrderCountVO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    // 添加订单数据并回填
    void insertOrder(OrderPO orderPO);

    // 删除订单（直接物理删除）
    @Delete("delete from orders where id = #{id}")
    void deleteOrder(Long id);

    // 查看用户对应的所有订单（只查询用户未删除的）
    @Select("select * from orders where user_id = #{userId} and user_delete = 0")
    List<OrderPO> orderList(Long userId);

    // 根据订单号查询对应订单
    @Select("select * from orders where order_no = #{orderNo}")
    OrderPO selectOrderByOrderNo(String orderNo);

    // 根据订单号更新订单状态
    @Update("update orders set status = #{status} where id = #{orderId}")
    void updateOrderStatus(Long orderId, Integer status);

    // 根据订单id查询订单
    @Select("select * from orders where id = #{id}")
    OrderPO byId(Long id);

    // 支付订单
    @Update(" update orders set payment_method = #{paymentMethod},status=1 where id = #{id}")
    void payOrder(PayOrderDTO payOrderDTO);

    // 根据订单ID列表查询订单（分页）- 用于商家查询自己的订单
    List<OrderPO> selectOrdersByIds(@Param("ids") List<Long> ids, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    // 根据订单ID列表和状态查询订单（分页）
    List<OrderPO> selectOrdersByIdsAndStatus(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    // 根据订单ID列表统计订单总数
    Long countOrdersByIds(@Param("ids") List<Long> ids);

    // 根据订单ID列表和状态统计订单总数
    Long countOrdersByIdsAndStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    // 订单取消（带原因）
    @Update("update orders set status = 4,cancel_reason = #{cancelReason} where id = #{orderId}")
    void cancelOrder(Long orderId,String cancelReason);

    // 用户逻辑删除订单（修改user_delete字段为1）
    @Update("update orders set user_delete = 1 where id = #{orderId}")
    void userDeleteOrder(Long orderId);

    // 商家逻辑删除订单（修改merchant_delete字段为1）
    @Update("update orders set merchant_delete = 1 where id = #{orderId}")
    void merchantDeleteOrder(Long orderId);

    // 查询创建时间超过三年且双方都逻辑删除的订单ID列表
    @Select("select id from orders where created_at < DATE_SUB(NOW(), INTERVAL 3 YEAR) and user_delete = 1 and merchant_delete = 1")
    List<Long> selectExpiredDeletedOrders();

    // 物理删除订单（用于定时任务清理）
    @Delete("delete from orders where id = #{orderId}")
    void physicalDeleteOrder(Long orderId);

    /**
     * 根据订单ID列表和日期范围统计每天的订单数量
     * @param orderIds 订单ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日订单数量统计列表
     */
    List<DailyOrderCountVO> selectDailyOrderCountByIds(@Param("orderIds") List<Long> orderIds,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    /**
     * 根据订单ID列表、日期范围和有效状态统计每天的有效订单数量
     * 有效订单排除状态：0-待支付，4-已取消，5-退单
     * @param orderIds 订单ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日有效订单数量统计列表
     */
    List<DailyOrderCountVO> selectDailyValidOrderCountByIds(@Param("orderIds") List<Long> orderIds,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    /**
     * 根据订单ID列表和日期范围统计每天的交易额
     * @param orderIds 订单ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日交易额统计列表
     */
    List<DailyAmountVO> selectDailyAmountByIds(@Param("orderIds") List<Long> orderIds,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * 根据订单ID列表、日期范围和有效状态统计每天的有效交易额
     * 有效订单排除状态：0-待支付，4-已取消，5-退单
     * @param orderIds 订单ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日有效交易额统计列表
     */
    List<DailyAmountVO> selectDailyValidAmountByIds(@Param("orderIds") List<Long> orderIds,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 根据商家ID查询交易构成统计数据（按商品分类统计）
     * 查询条件：
     * 1. 订单详情表中的商家ID匹配
     * 2. 关联订单主表，状态为 1-已支付，2-已发货，3-已完成，4-已取消
     * 3. 关联商品表，按商品分类(category)分组统计数量
     * @param merchantId 商家ID
     * @return 商品分类及其对应的数量列表
     */
    List<Map<String, Object>> selectTransactionStructureByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 订单退款（将订单状态修改为5-退单）
     * @param orderId 订单ID
     */
    @Update("update orders set status = 5 where id = #{orderId}")
    void refundOrder(Long orderId);
}

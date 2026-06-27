package com.clou.orderdemo.mapper;

import com.clou.orderdemo.POJO.po.OrderItemsPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemsMapper {

    @Insert("insert into order_items(order_id, product_id, product_name, price, quantity, subtotal, merchant_id, user_id, user_address,order_no) " +
            "values(#{orderId}, #{productId}, #{productName}, #{price}, #{quantity}, #{subtotal}, #{merchantId}, #{userId}, #{userAddress}, #{orderNo})")
    void insertOrderItems(OrderItemsPO orderItemsPO);

    @Delete("delete from order_items where order_id = #{orderId}")
    void deleteOrderItems(Long orderId);

    @Select("select * from order_items where order_id = #{orderId}")
    List<OrderItemsPO> byOrderId(Long orderId);

    // 根据商家ID查询关联的订单ID列表（去重）
    @Select("select distinct order_id from order_items where merchant_id = #{merchantId}")
    List<Long> selectOrderIdsByMerchantId(Long merchantId);

    // 根据商家ID和订单号查询关联的订单ID列表（去重）
    @Select("select distinct order_id from order_items where merchant_id = #{merchantId} and order_no = #{orderNo}")
    List<Long> selectOrderIdsByMerchantIdAndOrderNo(Long merchantId, String orderNo);

}

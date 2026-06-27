package com.clou.orderdemo.service.impl;

import com.clou.orderdemo.POJO.po.OrderItemsPO;
import com.clou.orderdemo.POJO.po.OrderPO;
import com.clou.orderdemo.mapper.OrderItemsMapper;
import com.clou.orderdemo.mapper.OrderMapper;
import com.clou.orderdemo.service.AdminOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemsMapper orderItemsMapper;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据订单号查询订单信息
     * @param orderNo
     * @return
     */
    @Override
    public OrderPO byOrderNo(String orderNo) {
        return orderMapper.selectOrderByOrderNo(orderNo);
    }

    /**
     * 根据订单号查询订单详情
     * @param orderId
     * @return
     */
    @Override
    public List<OrderItemsPO> byId(Long orderId) {
        List<OrderItemsPO> list= orderItemsMapper.byOrderId(orderId);
        List<OrderItemsPO> orderItemsPOList=new ArrayList<OrderItemsPO>();
        // 要反序列地址字段为json
        for (OrderItemsPO orderItemsPO : list) {
            OrderItemsPO orderItemsPO1=new OrderItemsPO();
            BeanUtils.copyProperties(orderItemsPO,orderItemsPO1);
            // 反序列化地址字段
            String addressJson = orderItemsPO.getUserAddress();
            if (addressJson != null) {
                try {
                    // 直接转为Map，避免定义实体类（适合简单场景）
                    Map<String, Object> addressMap = objectMapper.readValue(addressJson, Map.class);
                    // 设置到VO中（注意：VO的userAddress字段需要改为Map类型）
                    orderItemsPO1.setUserAddress(addressMap.toString());
                } catch (JsonProcessingException e) {
                    // 异常处理：保留原始字符串或设置空对象
                    orderItemsPO1.setUserAddress(addressJson);
                    // log.error("解析用户地址失败", e);
                }
            }
            orderItemsPOList.add(orderItemsPO1);
        }
        return orderItemsPOList;
    }

    /**
     * 订单退款
     * @param orderId 订单ID
     */
    @Override
    public void refundOrder(Long orderId) {
        orderMapper.refundOrder(orderId);
    }
}

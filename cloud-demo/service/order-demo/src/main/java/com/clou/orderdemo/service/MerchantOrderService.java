package com.clou.orderdemo.service;

import com.clou.orderdemo.POJO.po.OrderItemsPO;
import com.clou.orderdemo.POJO.vo.OrderVO;
import com.clou.orderdemo.POJO.vo.PageVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface MerchantOrderService {

    // 商户查看自己的订单列表（合并接口，支持状态筛选和订单号查询）
    PageVO<OrderVO> listOrders(Long merchantId, Integer status, String orderNo, Integer page, Integer pageSize);

    // 商户根据订单号查询订单详情
    List<OrderItemsPO> byId(Long orderId) throws JsonProcessingException;

    // 商家手动取消订单
    void cancel(Long orderId, String reason);

    // 商家发货
    void confirm(Long orderId);

    // 商家逻辑删除订单（修改merchant_delete字段）
    void delete(Long orderId);
}

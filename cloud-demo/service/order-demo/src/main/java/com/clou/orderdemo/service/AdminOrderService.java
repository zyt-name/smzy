package com.clou.orderdemo.service;

import com.clou.orderdemo.POJO.po.OrderItemsPO;
import com.clou.orderdemo.POJO.po.OrderPO;

import java.util.List;

public interface AdminOrderService {

    OrderPO byOrderNo(String orderNo);

    List<OrderItemsPO> byId(Long orderId);

    void refundOrder(Long orderId);

}

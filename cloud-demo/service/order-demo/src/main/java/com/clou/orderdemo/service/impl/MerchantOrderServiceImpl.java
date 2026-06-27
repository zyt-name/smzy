package com.clou.orderdemo.service.impl;

import com.clou.common.exception.BusinessException;
import com.clou.orderdemo.POJO.po.OrderItemsPO;
import com.clou.orderdemo.POJO.po.OrderPO;
import com.clou.orderdemo.POJO.vo.OrderVO;
import com.clou.orderdemo.POJO.vo.PageVO;
import com.clou.orderdemo.mapper.OrderItemsMapper;
import com.clou.orderdemo.mapper.OrderMapper;
import com.clou.orderdemo.service.MerchantOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantOrderServiceImpl implements MerchantOrderService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemsMapper orderItemsMapper;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 商户查看自己的订单列表（合并接口）
     * 查询逻辑：先通过merchant_id查询order_items表获取关联的order_id列表（去重），再查询orders主表
     * @param merchantId
     * @param status
     * @param orderNo
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<OrderVO> listOrders(Long merchantId, Integer status, String orderNo, Integer page, Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        List<Long> orderIds;

        // 如果传了订单号，优先按订单号查询
        if (orderNo != null && !orderNo.isEmpty()) {
            orderIds = orderItemsMapper.selectOrderIdsByMerchantIdAndOrderNo(merchantId, orderNo);
        } else {
            // 否则查询该商家所有的订单ID
            orderIds = orderItemsMapper.selectOrderIdsByMerchantId(merchantId);
        }

        // 如果没有关联的订单，返回空结果
        if (orderIds == null || orderIds.isEmpty()) {
            return new PageVO<>(0L, new ArrayList<>());
        }

        // 根据是否有状态筛选，调用不同的查询方法
        List<OrderPO> orderPOList;
        Long total;

        if (status != null) {
            orderPOList = orderMapper.selectOrdersByIdsAndStatus(orderIds, status, offset, pageSize);
            total = orderMapper.countOrdersByIdsAndStatus(orderIds, status);
        } else {
            orderPOList = orderMapper.selectOrdersByIds(orderIds, offset, pageSize);
            total = orderMapper.countOrdersByIds(orderIds);
        }

        // 转换为 OrderVO 并解析收货地址信息
        List<OrderVO> orderVOList = new ArrayList<>();
        for (OrderPO orderPO : orderPOList) {
            OrderVO orderVO = convertToOrderVO(orderPO, merchantId);
            orderVOList.add(orderVO);
        }

        return new PageVO<>(total, orderVOList);
    }

    /**
     * 将 OrderPO 转换为 OrderVO，并解析收货地址信息
     * @param orderPO
     * @param merchantId
     * @return
     */
    private OrderVO convertToOrderVO(OrderPO orderPO, Long merchantId) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderPO, orderVO);

        // 从 order_items 表中获取该订单该商家的 user_address
        List<OrderItemsPO> items = orderItemsMapper.byOrderId(orderPO.getId());
        String userAddressJson = null;
        for (OrderItemsPO item : items) {
            if (item.getMerchantId().equals(merchantId) && item.getUserAddress() != null) {
                userAddressJson = item.getUserAddress();
                break;
            }
        }

        // 解析 user_address JSON
        if (userAddressJson != null && !userAddressJson.isEmpty()) {
            try {
                Map<String, Object> addressMap = objectMapper.readValue(userAddressJson, Map.class);

                // 构建收货地址信息（province + city + district + detailAddress）
                Map<String, Object> shippingAddress = new HashMap<>();
                if (addressMap.containsKey("province")) {
                    shippingAddress.put("province", addressMap.get("province"));
                }
                if (addressMap.containsKey("city")) {
                    shippingAddress.put("city", addressMap.get("city"));
                }
                if (addressMap.containsKey("district")) {
                    shippingAddress.put("district", addressMap.get("district"));
                }
                if (addressMap.containsKey("detailAddress")) {
                    shippingAddress.put("detailAddress", addressMap.get("detailAddress"));
                }
                orderVO.setShippingAddress(shippingAddress);

                // 构建收货人信息（receiverName + receiverPhone）
                Map<String, Object> shippingInformation = new HashMap<>();
                if (addressMap.containsKey("receiverName")) {
                    shippingInformation.put("receiverName", addressMap.get("receiverName"));
                }
                if (addressMap.containsKey("receiverPhone")) {
                    shippingInformation.put("receiverPhone", addressMap.get("receiverPhone"));
                }
                orderVO.setShippingInformation(shippingInformation);

            } catch (JsonProcessingException e) {
                // JSON 解析失败，设置空值
                orderVO.setShippingAddress(new HashMap<>());
                orderVO.setShippingInformation(new HashMap<>());
            }
        } else {
            // 没有地址信息，设置空值
            orderVO.setShippingAddress(new HashMap<>());
            orderVO.setShippingInformation(new HashMap<>());
        }

        return orderVO;
    }

    /**
     * 根据订单ID查询订单详情
     * @param orderId
     * @return
     */
    @Override
    public List<OrderItemsPO> byId(Long orderId) throws JsonProcessingException {
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
     * 取消订单
     * @param orderId
     * @param reason
     */
    @Override
    public void cancel(Long orderId, String reason) {
        if (orderMapper.byId(orderId).getStatus()==1 || orderMapper.byId(orderId).getStatus()==0) {
            orderMapper.cancelOrder(orderId, reason);
        } else {
            throw new BusinessException(0,"订单状态不允许取消");
        }

    }

    /**
     * 商家发货
     * @param orderId
     */
    @Override
    public void confirm(Long orderId) {
        if (orderMapper.byId(orderId).getStatus()==1) {
            orderMapper.updateOrderStatus(orderId, 2);
        }else {
            throw new BusinessException(0,"订单状态不允许发货");
        }
    }

    /**
     * 商家逻辑删除订单（修改merchant_delete字段为1）
     * @param orderId
     */
    @Override
    @Transactional
    public void delete(Long orderId) {
        // 验证订单状态，只有已完成(3)或已取消(4)的订单才能删除
        Integer status = orderMapper.byId(orderId).getStatus();
        if (status == null || (status != 3 && status != 4)) {
            throw new BusinessException(500, "订单状态错误，无法删除！");
        }
        orderMapper.merchantDeleteOrder(orderId);
    }

}

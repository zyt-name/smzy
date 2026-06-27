package com.clou.orderdemo.service.impl;

import com.clou.apidemo.POJO.vo.ShopDetailsVO;
import com.clou.apidemo.POJO.vo.UserCartVO;
import com.clou.apidemo.client.ShopClient;
import com.clou.common.exception.BusinessException;
import com.clou.common.mqPO.orderCartMessage;
import com.clou.common.util.OrderNumberGenerator;
import com.clou.orderdemo.POJO.dto.PayOrderDTO;
import com.clou.orderdemo.POJO.po.OrderItemsPO;
import com.clou.orderdemo.POJO.po.OrderPO;
import com.clou.orderdemo.POJO.vo.OrderItemsVO;
import com.clou.orderdemo.POJO.vo.OrderVO;
import com.clou.orderdemo.mapper.OrderItemsMapper;
import com.clou.orderdemo.mapper.OrderMapper;
import com.clou.orderdemo.mq.producer.OrderCartProducer;
import com.clou.orderdemo.mq.producer.OrderProducer;
import com.clou.orderdemo.service.UserOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Service
public class UserOrderServiceImpl implements UserOrderService {

    @Autowired
    OrderItemsMapper orderItemsMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OrderCartProducer orderCartProducer;
    @Autowired
    OrderProducer orderProducer;
    @Autowired
    ShopClient shopClient;


    /**
     * 添加订单
     * @param userCarts
     * @param userAddress
     * @return 订单ID
     */
    // TODO 后期加入优惠券功能，需要先在父订单表中加入优惠券id字段和优惠后价格字段，然后还要创建优惠券表，用户关联优惠券表
    @Override
    @Transactional
    public Long addOrder(List<UserCartVO> userCarts,String userAddress,String remark) {
        if (userCarts == null || userCarts.size() == 0){
            throw new BusinessException(0,"购物车不能为空");
        }else {
            // 生成订单号
            OrderNumberGenerator generator = new OrderNumberGenerator();
            String orderNo = generator.generateOrderNumber();
            Long userId = null;
            // 先强循环获取总的价格和用户id
            float totalPrice = 0;
            for (UserCartVO userCartVO : userCarts) {
                // 获取用户id
                userId = userCartVO.getUserId();
                // 计算总价格
                totalPrice += userCartVO.getPrice() * userCartVO.getProductCount();
            }

            // 创建订单数据对象,设置订单号
            OrderPO orderPO = new OrderPO();
            orderPO.setOrderNo(orderNo);
            orderPO.setTotalPrice(totalPrice);
            orderPO.setUserId(userId);
            orderPO.setStatus(0);
            orderPO.setRemark(remark);
            orderPO.setCreatedAt(LocalDateTime.now());
            orderPO.setUpdatedAt(LocalDateTime.now());
            orderPO.setUserDelete(0);
            orderPO.setMerchantDelete(0);
            // 插入订单数据到订单父表中,并回填订单id
            orderMapper.insertOrder(orderPO);
            // 获取回填后的订单id
            Long orderId = orderPO.getId();

            // 先强循环遍历传过来的购物车数据，创建商品订单数据，添加到商品订单表中
            for (UserCartVO userCartVO : userCarts) {
                OrderItemsPO orderItemsPO = new OrderItemsPO();

                // TODO 用rabbitMQ/feign和分布式锁扣除库存，并更新库存状态

                BeanUtils.copyProperties(userCartVO, orderItemsPO);
                orderItemsPO.setPrice(userCartVO.getPrice());
                orderItemsPO.setQuantity(Math.toIntExact(userCartVO.getProductCount()));
                // 设置订单id
                orderItemsPO.setOrderId(orderId);
                // 设置订单号
                orderItemsPO.setOrderNo(orderNo);
                // 设置地址
                orderItemsPO.setUserAddress(userAddress);
                // 计算该商品的总价格
                float subtotal = userCartVO.getPrice() * userCartVO.getProductCount();
                orderItemsPO.setSubtotal(subtotal);
                // 如果merchantId为空，从商品服务查询
                if (orderItemsPO.getMerchantId() == null && userCartVO.getProductId() != null) {
                    ShopDetailsVO product = shopClient.byShopIdDetails(userCartVO.getProductId());
                    if (product != null) {
                        orderItemsPO.setMerchantId(product.getMerchantId());
                    }
                }
                // 插入商品订单数据到商品订单表中
                orderItemsMapper.insertOrderItems(orderItemsPO);
            }
            // 用rabbitMQ清除购物车数据
            orderCartMessage orderCartMessage = new orderCartMessage();
            orderCartMessage.setUserId(userId);
            orderCartMessage.setOrderNo(orderNo);
            // 简化：直接发送对象，无需手动转JSON
            orderCartProducer.send(orderCartMessage);

            // 订单定时任务，定时更新订单状态（待支付）
            orderProducer.orderTimeout(String.valueOf(orderId));

            return orderId;
        }
    }

    /**
     * 添加单个商品订单
     * @param userId 用户id
     * @param productId 商品id
     * @param quantity 购买数量
     * @param userAddress 用户地址
     * @param remark 备注
     * @return 订单ID
     */
    @Override
    @Transactional
    public Long addSingleOrder(Long userId, Long productId, Integer quantity, String userAddress, String remark) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(0, "购买数量必须大于0");
        }

        // 查询商品信息
        ShopDetailsVO product = shopClient.byShopIdDetails(productId);
        if (product == null) {
            throw new BusinessException(0, "商品不存在");
        }

        // 生成订单号
        OrderNumberGenerator generator = new OrderNumberGenerator();
        String orderNo = generator.generateOrderNumber();

        // 计算总价格
        float totalPrice = product.getPrice() * quantity;

        // 创建订单数据对象,设置订单号
        OrderPO orderPO = new OrderPO();
        orderPO.setOrderNo(orderNo);
        orderPO.setTotalPrice(totalPrice);
        orderPO.setUserId(userId);
        orderPO.setStatus(0);
        orderPO.setRemark(remark);
        orderPO.setCreatedAt(LocalDateTime.now());
        orderPO.setUpdatedAt(LocalDateTime.now());
        orderPO.setUserDelete(0);
        orderPO.setMerchantDelete(0);
        // 插入订单数据到订单父表中,并回填订单id
        orderMapper.insertOrder(orderPO);
        // 获取回填后的订单id
        Long orderId = orderPO.getId();

        // 创建商品订单数据
        OrderItemsPO orderItemsPO = new OrderItemsPO();

        // TODO 用rabbitMQ/feign和分布式锁扣除库存，并更新库存状态

        orderItemsPO.setProductId(productId);
        orderItemsPO.setMerchantId(product.getMerchantId());
        orderItemsPO.setProductName(product.getName());
        orderItemsPO.setPrice(product.getPrice());
        orderItemsPO.setQuantity(quantity);
        // 设置用户id
        orderItemsPO.setUserId(userId);
        // 设置订单id
        orderItemsPO.setOrderId(orderId);
        // 设置订单号
        orderItemsPO.setOrderNo(orderNo);
        // 设置地址
        orderItemsPO.setUserAddress(userAddress);
        // 计算该商品的总价格
        float subtotal = product.getPrice() * quantity;
        orderItemsPO.setSubtotal(subtotal);
        // 插入商品订单数据到商品订单表中
        orderItemsMapper.insertOrderItems(orderItemsPO);

        // 订单定时任务，定时更新订单状态（待支付）
        orderProducer.orderTimeout(String.valueOf(orderId));

        return orderId;
    }

    /**
     * 用户逻辑删除订单（修改user_delete字段为1）
     * @param orderId
     */
    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        // 验证订单状态，只有已完成(3)或已取消(4)的订单才能删除
        Integer status = orderMapper.byId(orderId).getStatus();
        if (status == null || (status != 3 && status != 4)) {
            throw new BusinessException(500, "订单状态错误，无法删除！");
        }
        orderMapper.userDeleteOrder(orderId);
    }

    /**
     * 获取订单列表
     * @param userId
     * @return
     */
    @Override
    public List<OrderVO> getOrderList(Long userId) {
        List<OrderPO> list = orderMapper.orderList(userId);
        List<OrderVO> voList = new ArrayList<OrderVO>();
        for (OrderPO orderPO : list) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orderPO, orderVO);
            voList.add(orderVO);
        }
        return voList;
    }

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    @Override
    public List<OrderItemsVO> getOrderItems(Long orderId) {
        List<OrderItemsPO> list = orderItemsMapper.byOrderId(orderId);
        List<OrderItemsVO> voItemsList = new ArrayList<OrderItemsVO>();

        OrderPO orderPO = orderMapper.byId(orderId);
        String cancelReason = orderPO != null ? orderPO.getCancelReason() : null;

        for (OrderItemsPO orderItemsPO : list) {
            OrderItemsVO orderItemsVO = new OrderItemsVO();
            BeanUtils.copyProperties(orderItemsPO, orderItemsVO);

            orderItemsVO.setCancelReason(cancelReason);
            String addressJson = orderItemsPO.getUserAddress();
            if (addressJson != null) {
                try {
                    Map<String, Object> addressMap = objectMapper.readValue(addressJson, Map.class);
                    orderItemsVO.setUserAddress(objectMapper.writeValueAsString(addressMap));
                } catch (JsonProcessingException e) {
                    orderItemsVO.setUserAddress(addressJson);
                }
            }
            voItemsList.add(orderItemsVO);
        }
        return voItemsList;
    }

    /**
     * 根据订单号获取订单详情
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO byOrderNo(String orderNo) {
        OrderPO orderPO = orderMapper.selectOrderByOrderNo(orderNo);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderPO, orderVO);
        return orderVO;
    }

    /**
     * 根据订单ID获取订单信息（用于支付页面）
     * @param orderId
     * @return
     */
    @Override
    public OrderVO getOrderById(Long orderId) {
        OrderPO orderPO = orderMapper.byId(orderId);
        if (orderPO == null) {
            return null;
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderPO, orderVO);
        return orderVO;
    }

    /**
     * 更新订单状态（0-待支付，1-已支付，2-已发货，3-已完成，4-已取消）
     * @param orderId
     * @param status
     */
    @Override
    public void updateOrderStatus(Long orderId, int status) {
        orderMapper.updateOrderStatus(orderId, status);
    }

    /**
     * 根据id获取订单信息
     * @param id
     * @return
     */
    @Override
    public OrderPO byId(Long id) {
        return orderMapper.byId(id);
    }

    /**
     * 支付订单（模拟支付，只是修改订单状态为已支付，实际场景需要调用第三方支付接口）
     * （0-待支付，1-已支付，2-已发货，3-已完成，4-已取消）
     * @param payOrderDTO
     */
    @Override
    @Transactional
    public void pay(PayOrderDTO payOrderDTO) {
        OrderPO orderPO = orderMapper.byId(payOrderDTO.getId());
        if (orderPO.getStatus() == 0) {
            orderMapper.payOrder(payOrderDTO);
        } else {
            throw new BusinessException(0,"订单状态不正确，无法支付");
        }
    }


    /**
     * 订单取消
     * @param orderId
     * @param cancelReason
     */
    @Override
    @Transactional
    public void cancel(Long orderId, String cancelReason) {
        if (orderMapper.byId(orderId).getStatus() == 0) {
            orderMapper.cancelOrder(orderId, cancelReason);
        }else {
            throw new BusinessException(0,"订单状态不正确，无法取消");
        }
    }

    /**
     * 确认收货
     * @param orderId
     */
    @Override
    public void confirmReceive(Long orderId) {
        if (orderMapper.byId(orderId).getStatus() == 2) {
            orderMapper.updateOrderStatus(orderId, 3);
        } else {
            throw new BusinessException(0,"订单状态不正确，无法确认收货");
        }
    }


}

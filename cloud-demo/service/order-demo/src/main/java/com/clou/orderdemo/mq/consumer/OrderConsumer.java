package com.clou.orderdemo.mq.consumer;

import com.clou.orderdemo.service.UserOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderConsumer {

    @Autowired
    UserOrderService userOrderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "orderTimeout",
                    durable = "true",
                    autoDelete = "false"  // 显式指定不自动删除
            ),
            exchange = @Exchange(
                    name = "orderExchange",
                    type = "x-delayed-message",  // 正确：指定延迟交换机类型
                    durable = "true",
                    autoDelete = "false",
                    arguments = {
                            @Argument(name = "x-delayed-type", value = "direct")  // 仅保留底层路由类型
                    }
            ),
            key = "orderTimeout"
    ))
    public void orderTimeout(String orderId) {
        log.info("订单 {} 自动检查超时", orderId);
        userOrderService.cancel(Long.parseLong(orderId),"订单超时");
        // TODO 订单超时提醒用户
    }
}

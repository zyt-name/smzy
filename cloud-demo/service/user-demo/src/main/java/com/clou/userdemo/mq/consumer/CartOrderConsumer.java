package com.clou.userdemo.mq.consumer;

import com.clou.common.mqPO.orderCartMessage;
import com.clou.userdemo.service.UserCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CartOrderConsumer {

    @Autowired
    UserCartService userCartService;
    @Autowired
    ObjectMapper objectMapper;

    // 核心注解：声明交换机、队列、绑定关系，并监听队列
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = "orderCart",  // 队列名称
                            durable = "true"  // 队列持久化（MQ重启后不丢失）
                    ),
                    exchange = @Exchange(
                            name = "orderCartExchange",  // 交换机名称
                            type = ExchangeTypes.TOPIC,  // 交换机类型
                            durable = "true"  // 交换机持久化
                    ),
                    key = "orderCart"  // 路由键（与生产者发送时的路由键需一致）
            )
    )
    public void deleteCart(orderCartMessage  orderCartMessage) {
        log.info("结算购物车消息{}", orderCartMessage);
        userCartService.deleteCart(orderCartMessage.getUserId());
    }

}

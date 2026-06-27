package com.clou.orderdemo.mq.producer;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    // 订单超时取消订单消息
    public void orderTimeout(String orderId) {

        // 定义路由键
        String routingKey = "orderTimeout";
        // 定义交换机名称
        String exchangeName = "orderExchange";
        // 发送消息时指定交换机、路由键
        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderId, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置消息过期时间为30分钟
                message.getMessageProperties().setHeader("x-delay", 1800000);
                return message;
            }
        });

    }

}

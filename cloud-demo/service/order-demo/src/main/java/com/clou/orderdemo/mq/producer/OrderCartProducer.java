package com.clou.orderdemo.mq.producer;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author LENOVO
 */
@Component
public class OrderCartProducer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    // message 就是所要发送的消息
    public void send(Object orderCartMessage) {

        // 定义路由键
        String routingKey = "orderCart";
        // 定义交换机名称
        String exchangeName = "orderCartExchange";
        // 发送消息时指定交换机、路由键
        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderCartMessage);

    }

}

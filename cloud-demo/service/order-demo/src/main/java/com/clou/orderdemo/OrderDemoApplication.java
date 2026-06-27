package com.clou.orderdemo;

import com.clou.apidemo.client.ShopClient;
import com.clou.apidemo.client.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.clou.orderdemo",
        "com.clou.common"
})
@EnableFeignClients(clients = {UserClient.class, ShopClient.class})
@EnableScheduling
public class OrderDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderDemoApplication.class, args);
    }

}

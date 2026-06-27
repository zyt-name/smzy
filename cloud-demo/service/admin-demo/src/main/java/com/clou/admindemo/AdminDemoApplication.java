package com.clou.admindemo;

import com.clou.apidemo.client.MerchantClient;
import com.clou.apidemo.client.ShopClient;
import com.clou.apidemo.client.UserClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "com.clou.admindemo",
        "com.clou.common",
        "com.clou.servicecommon"
})
@EnableFeignClients(clients = {
        UserClient.class,
        ShopClient.class,
        MerchantClient.class})
@MapperScan({"com.clou.admindemo.mapper", "com.clou.servicecommon.mapper"})
public class AdminDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminDemoApplication.class, args);
    }

}

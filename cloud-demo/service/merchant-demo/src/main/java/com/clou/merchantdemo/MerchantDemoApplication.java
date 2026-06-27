package com.clou.merchantdemo;

import com.clou.apidemo.client.ShopClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "com.clou.merchantdemo",
        "com.clou.common",
        "com.clou.servicecommon"
})
@EnableFeignClients(clients ={ShopClient.class})
@MapperScan({"com.clou.merchantdemo.mapper", "com.clou.servicecommon.mapper"})
public class MerchantDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantDemoApplication.class, args);
    }

}

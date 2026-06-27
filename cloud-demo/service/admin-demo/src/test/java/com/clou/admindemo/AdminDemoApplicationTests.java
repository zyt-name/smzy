package com.clou.admindemo;

import com.clou.admindemo.POJO.po.AdminPO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminDemoApplicationTests {

    @Test
    void contextLoads() {

        AdminPO adminPO = new AdminPO();
        adminPO.setId(1L);
        System.out.println(adminPO.getId());

    }

}

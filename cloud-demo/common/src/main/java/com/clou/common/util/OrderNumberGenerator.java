package com.clou.common.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class OrderNumberGenerator {


    /**
     * 生成订单号
     */
    public String generateOrderNumber() {
        // 1. 获取当前时间的毫秒数后6位
        SimpleDateFormat sdf = new SimpleDateFormat("SSS");
        String timeStr = sdf.format(new Date());
        // 取毫秒数后6位
        long currentTime = System.currentTimeMillis() % 1000000;
        String timePart = String.format("%06d", currentTime);

        // 2. 生成4位随机数
        Random random = new Random();
        // 生成1000-9999之间的随机数
        int randomNum = random.nextInt(9000) + 1000;

        // 3. 组合成10位订单号
        return timePart + randomNum;
    }
}

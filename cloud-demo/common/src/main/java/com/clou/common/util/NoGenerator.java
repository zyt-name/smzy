package com.clou.common.util;

import java.util.Random;

public class NoGenerator {

    // 全局唯一Random实例，避免频繁创建
    private static final Random RANDOM = new Random();

    /**
     * 生成10位唯一随机数（核心方法）
     * 格式：固定10位数字，范围 0000000000 ~ 9999999999
     * 实现逻辑：时间戳后8位 + 2位随机数，保证高并发下唯一性
     * @return 10位随机数字符串（带前导0，确保长度）
     */
    public static String generate10UniqueNum() {
        // 1. 获取当前毫秒级时间戳，截取后8位（保证随时间变化）
        String timePart = String.valueOf(System.currentTimeMillis()).substring(4);
        // 2. 生成2位随机数，补前导0确保固定2位（避免1位的情况）
        String randomPart = String.format("%02d", RANDOM.nextInt(100));
        // 3. 拼接成10位，返回结果
        return timePart + randomPart;
    }
}

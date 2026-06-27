package com.clou.common.util;


import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;


import java.util.concurrent.TimeUnit;

/**
 * Sa-Token 公共工具类（放入公共模块，仅提供：生成令牌 + 存入 Redis 功能）
 */
@Slf4j
public class SaTokenHelper {

    // 完整的 Sa-Token 登录流程，增加 type 参数区分不同身份
    public static String loginAndGenerateToken(String type, Long id, String name, String userNo) {
        // 1. 拼接带身份的唯一标识，例如 "admin:1"
        String loginKey = type + ":" + id;
        // 2. Sa-Token 登录
        StpUtil.login(loginKey);
        // 3. 获取当前 Session，存入 name、type 和 userNo
        StpUtil.getSession().set("username", name);
        StpUtil.getSession().set("type", type);
        StpUtil.getSession().set("userNo", userNo);
        // 4. 获取token
        String token = StpUtil.getTokenValue();
        log.info("用户登录成功：type={}, id={}, name={}, userNo={}, token={}", type, id, name, userNo, token);
        return token;
    }
}

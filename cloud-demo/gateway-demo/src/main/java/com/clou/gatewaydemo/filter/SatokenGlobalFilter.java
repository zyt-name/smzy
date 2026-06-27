package com.clou.gatewaydemo.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import com.clou.gatewaydemo.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.clou.common.constant.code.unLoginCode;

@Component
@Slf4j
public class SatokenGlobalFilter implements GlobalFilter, Ordered {

    // 定义不需要Sa-Token校验的白名单路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/merchant-demo/merchant/login",
            "/admin-demo/admin/login",
            "/user-demo/users/login",
            "/admin-demo/admin/add",
            "/user-demo/users/addUser",
            "/merchant-demo/merchant/add",
            "/user-demo/user/shop/list",
            "/user-demo/user/shop/recommend",
            "/user-demo/user/shop/categoryList",
            "/user-demo/user/banner/list",
            "/user-demo/user/shop/merchantList"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getURI().toString();
        String requestPath = request.getURI().getPath();
        log.info("请求：{}", requestUri);

        // 2. 检查是否在白名单中
        boolean isWhitelisted = WHITE_LIST.stream().anyMatch(url -> url.equals(requestPath));
        
        if (isWhitelisted) {
            log.info("白名单路径：{}，尝试解析token", requestPath);
            try {
                String loginKey = (String) StpUtil.getLoginId();
                String username = (String) StpUtil.getSession().get("username");
                String userId = loginKey;
                if (loginKey.contains(":")) {
                    userId = loginKey.split(":")[1];
                }
                String tokenValue = StpUtil.getTokenValue();
                // 对中文用户名进行URL编码，防止传输过程中乱码
                String encodedUsername = SaFoxUtil.isEmpty(username) ? "" : URLEncoder.encode(username, StandardCharsets.UTF_8);
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("id", userId)
                        .header("username", encodedUsername)
                        .header("token", SaFoxUtil.isEmpty(tokenValue) ? "" : tokenValue)
                        .build();
                ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
                log.info("白名单路径解析token成功，id:{}, name:{}", userId, username);
                return chain.filter(modifiedExchange);
            } catch (Exception e) {
                log.info("白名单路径无token，直接放行：{}", requestPath);
                return chain.filter(exchange);
            }
        }

        // 3. 使用 Sa-Token 原生 API 校验登录状态
        // getLoginId() 会自动从请求头（默认为 satoken）中获取 token 并验证
        // 如果 token 无效或不存在，会抛出 NotLoginException
        try {
            // 1. 获取带前缀的 loginId (例如 "admin:1")
            String loginKey = (String) StpUtil.getLoginId();
            String username = (String) StpUtil.getSession().get("username");
            
            log.info("Sa-Token 校验成功，LoginKey: {}, 用户名: {}", loginKey, username);

            // 2. 解析出原始数字 ID (去掉 ":" 之前的内容)
            String userId = loginKey;
            if (loginKey.contains(":")) {
                userId = loginKey.split(":")[1];
            }

            String tokenValue = StpUtil.getTokenValue();
            // 3. 将纯数字 ID 和用户名、token存入请求头
            // 对中文用户名进行URL编码，防止传输过程中乱码
            String encodedUsername = SaFoxUtil.isEmpty(username) ? "" : URLEncoder.encode(username, StandardCharsets.UTF_8);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("id", userId)
                    .header("username", encodedUsername)
                    .header("token", SaFoxUtil.isEmpty(tokenValue) ? "" : tokenValue)
                    .build();
            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            log.info("解析的id:{},name:{}",userId,username);
            // 4. 放行
            return chain.filter(modifiedExchange);
        } catch (Exception e) {
            log.warn("Sa-Token 校验失败，请求 URI: {}, 错误信息: {}", requestUri, e.getMessage());
            throw new BusinessException(unLoginCode, "用户未登录");
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
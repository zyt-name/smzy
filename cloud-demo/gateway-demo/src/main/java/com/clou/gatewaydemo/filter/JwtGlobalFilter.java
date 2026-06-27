//package com.clou.gatewaydemo.filter;
//
//
//
//import com.clou.gatewaydemo.exception.BusinessException;
//import com.clou.gatewaydemo.util.JwtUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import static com.clou.common.constant.code.unLoginCode;
//
//@Component
//@Slf4j
//public class JwtGlobalFilter implements GlobalFilter, Ordered {
//
//    // 定义不需要JWT校验的路径
//    private static final List<String> WHITE_LIST = Arrays.asList(
//            "/login",
//            "http://localhost:8080/admin-demo/admin/add",
//            "http://localhost:8080/user-demo/users/addUser",
//            "http://localhost:8080/merchant-demo/merchant/add",
//            "http://localhost:8080/user-demo/user/shop/list"
//    );
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        // 获取业务请求
//        ServerHttpRequest request = exchange.getRequest();
//        log.info("请求：{}", request.getURI());
//
//        // 检查是否在白名单中
//        String requestUri = request.getURI().toString();
//        String requestPath = request.getURI().getPath();
//
//        boolean isWhitelisted = WHITE_LIST.stream().anyMatch(url ->
//                url.equals(requestPath) ||
//                        (url.startsWith("/") && requestPath.contains(url)) ||
//                        requestUri.equals(url)
//        );
//        // 如果在白名单中，则直接放行
//        if (isWhitelisted) {
//            return chain.filter(exchange);
//        }
//
//        // 获取请求头token
//        String token = request.getHeaders().getFirst("token");
//
//        // 用jwt工具校验token并将获取到的id和username存入请求头中，继续给子业务系统处理
//        // 校验token并提取用户信息
//        Map<String, Object> userInfo;
//        try {
//            userInfo = JwtUtil.parseToken(token);
//        } catch (Exception e) {
//            // token无效或过期，抛出业务异常
//            throw new BusinessException(unLoginCode, "用户未登录");
//        }
//
//        // 将获取到的id和username存入请求头中，继续给子业务系统处理
//        ServerHttpRequest modifiedRequest = request.mutate()
//                .header("id", userInfo.get("id").toString())
//                .header("username", userInfo.get("username").toString())
//                .build();
//
//        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
//
//        // 放行
//        return chain.filter(exchange);
//
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}

package com.clou.gatewaydemo.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class SaTokenConfigure implements WebFluxConfigurer {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 开放地址
                .addExclude("/favicon.ico")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 这里可以添加额外的校验逻辑
                })
                // 异常处理方法：每次 setAuth 函数抛出异常时进入
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                });
    }
}
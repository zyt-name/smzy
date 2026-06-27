package com.clou.servicecommon.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    /**
     * 日志描述，如 "登录"、"登出"
     */
    String value() default "";
}

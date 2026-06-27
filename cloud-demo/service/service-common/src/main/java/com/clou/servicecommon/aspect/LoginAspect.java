package com.clou.servicecommon.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.clou.servicecommon.annotation.LogAnnotation;
import com.clou.servicecommon.mapper.LoginLogMapper;
import com.clou.servicecommon.pojo.LoginLogPO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoginAspect {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Pointcut("@annotation(com.clou.servicecommon.annotation.LogAnnotation)")
    public void loginLogPointcut() {}

    @Around("loginLogPointcut()")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        
        // 获取方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        
        // 获取注解值
        LogAnnotation logAnnotation = signature.getMethod().getAnnotation(LogAnnotation.class);
        String operation = logAnnotation != null ? logAnnotation.value() : "未知操作";
        
        // 获取参数
        Object[] args = joinPoint.getArgs();
        String params = Arrays.toString(args);
        
        // 尝试获取当前登录用户信息 (如果已登录)
        String currentUserInfo = "未登录";
        String currentUserName = "未知";
        String currentUserNo = "未知";
        try {
            if (StpUtil.isLogin()) {
                Object loginId = StpUtil.getLoginIdDefaultNull();
                Object username = StpUtil.getSession().get("username");
                Object userNo = StpUtil.getSession().get("userNo");
                currentUserNo = String.valueOf(userNo);
                currentUserName = String.valueOf(username);
                currentUserInfo = String.format("账号ID: %s, 用户名: %s, 用户编号: %s", loginId, username, userNo);
            }
        } catch (Exception e) {
            // 忽略网关等特殊环境下的异常
        }

        log.info("开始执行 [{}] 操作, 当前用户: {}, 方法: {}, 参数: {}", operation, currentUserInfo, methodName, params);
        
        Object result = null;
        try {
            result = joinPoint.proceed();
            
            // 执行后再次检查登录状态 (处理登录接口成功后的情况)
            if (currentUserInfo.equals("未登录")) {
                try {
                    if (StpUtil.isLogin()) {
                        Object loginId = StpUtil.getLoginId();
                        Object username = StpUtil.getSession().get("username");
                        Object userNo = StpUtil.getSession().get("userNo");
                        currentUserNo = String.valueOf(userNo);
                        currentUserName = String.valueOf(username);
                        currentUserInfo = String.format("账号ID: %s, 用户名: %s, 用户编号: %s", loginId, username, userNo);
                        log.info("[{}] 执行成功, 识别到新登录用户: {}", operation, currentUserInfo);
                    }
                } catch (Exception ignored) {}
            }
            
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("完成执行 [{}] 操作, 耗时: {}ms, 返回结果: {}", operation, (endTime - beginTime), result);

            // 保存日志到数据库 (仅记录包含"登录"或"退出/登出"的操作)
            if (operation.contains("登录") || operation.contains("退出") || operation.contains("登出")) {
                try {
                    LoginLogPO logPO = new LoginLogPO();
                    logPO.setUserName(currentUserName);
                    logPO.setUserNo(currentUserNo);
                    logPO.setOperation(operation);
                    logPO.setOperateTime(LocalDateTime.now());
                    loginLogMapper.insert(logPO);
                    log.info("日志已成功保存到数据库: {}", logPO);
                } catch (Exception e) {
                    log.error("保存日志到数据库失败: {}", e.getMessage());
                }
            }
        }
    }

}

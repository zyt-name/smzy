package com.clou.servicecommon.aspect;

import com.clou.servicecommon.annotation.UserOperationLog;
import com.clou.servicecommon.mapper.UserOperationLogMapper;
import com.clou.servicecommon.pojo.UserOperationLogPO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Component
@Slf4j
public class UserOperationLogAspect {

    @Autowired
    private UserOperationLogMapper userOperationLogMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(com.clou.servicecommon.annotation.UserOperationLog)")
    public void userOperationLogPointcut() {}

    @Around("userOperationLogPointcut()")
    public Object recordUserOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        UserOperationLog annotation = method.getAnnotation(UserOperationLog.class);
        
        int operateType = annotation.operateType();
        Long userId = extractUserId(joinPoint);
        
        Object result = joinPoint.proceed();
        
        List<Long> productIds = extractProductIds(joinPoint, result, operateType);
        
        if (userId != null && !productIds.isEmpty()) {
            saveOperationLog(userId, operateType, productIds);
        }
        
        return result;
    }

    private Long extractUserId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        
        for (int i = 0; i < paramNames.length; i++) {
            if ("userId".equals(paramNames[i]) || "id".equals(paramNames[i])) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                }
            }
        }
        
        return null;
    }

    private List<Long> extractProductIds(ProceedingJoinPoint joinPoint, Object result, int operateType) {
        List<Long> productIds = new ArrayList<>();
        
        try {
            switch (operateType) {
                case 1:
                    productIds = extractFromCartResult(result);
                    break;
                case 2:
                    productIds = extractFromAddCartArgs(joinPoint);
                    break;
                case 3:
                    productIds = extractFromCartArgs(joinPoint);
                    break;
                case 5:
                    productIds = extractFromPayArgs(joinPoint);
                    break;
                case 6:
                    productIds = extractFromConfirmReceiveArgs(joinPoint);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("提取商品ID失败", e);
        }
        
        return productIds;
    }

    private List<Long> extractFromCartResult(Object result) {
        List<Long> productIds = new ArrayList<>();
        
        Object data = result;
        if (result != null) {
            try {
                Class<?> resultClass = result.getClass();
                java.lang.reflect.Method getDataMethod = resultClass.getMethod("getData");
                Object dataValue = getDataMethod.invoke(result);
                if (dataValue != null) {
                    data = dataValue;
                }
            } catch (Exception e) {
                log.debug("无法从Result中提取data: {}", e.getMessage());
            }
        }
        
        if (data != null) {
            try {
                Class<?> dataClass = data.getClass();
                java.lang.reflect.Method getDataMethod = dataClass.getMethod("getData");
                Object innerData = getDataMethod.invoke(data);
                if (innerData != null) {
                    data = innerData;
                }
            } catch (Exception e) {
                log.debug("无法从PageVO中提取data: {}", e.getMessage());
            }
        }
        
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (Object item : list) {
                Long productId = extractIdFromObject(item);
                if (productId != null) {
                    productIds.add(productId);
                }
            }
        }
        
        return productIds;
    }

    private List<Long> extractFromAddCartArgs(ProceedingJoinPoint joinPoint) {
        List<Long> productIds = new ArrayList<>();
        Object[] args = joinPoint.getArgs();
        
        for (Object arg : args) {
            Long productId = extractProductIdFromObject(arg);
            if (productId != null) {
                productIds.add(productId);
                break;
            }
        }
        
        return productIds;
    }

    private List<Long> extractFromCartArgs(ProceedingJoinPoint joinPoint) {
        List<Long> productIds = new ArrayList<>();
        Object[] args = joinPoint.getArgs();
        
        for (Object arg : args) {
            if (arg instanceof List) {
                List<?> list = (List<?>) arg;
                for (Object item : list) {
                    Long productId = extractProductIdFromObject(item);
                    if (productId != null) {
                        productIds.add(productId);
                    }
                }
                break;
            }
        }
        
        return productIds;
    }

    private List<Long> extractFromPayArgs(ProceedingJoinPoint joinPoint) {
        List<Long> productIds = new ArrayList<>();
        Object[] args = joinPoint.getArgs();
        
        for (Object arg : args) {
            Long orderId = extractIdFromObject(arg);
            if (orderId != null) {
                productIds = getProductIdsByOrderId(orderId);
                break;
            }
        }
        
        return productIds;
    }

    private List<Long> extractFromConfirmReceiveArgs(ProceedingJoinPoint joinPoint) {
        List<Long> productIds = new ArrayList<>();
        Object[] args = joinPoint.getArgs();
        
        for (Object arg : args) {
            if (arg instanceof Long) {
                productIds = getProductIdsByOrderId((Long) arg);
                break;
            }
        }
        
        return productIds;
    }

    private Long extractProductIdFromObject(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            Class<?> clazz = obj.getClass();
            
            try {
                java.lang.reflect.Field productIdField = clazz.getDeclaredField("productId");
                productIdField.setAccessible(true);
                Object value = productIdField.get(obj);
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof Integer) {
                    return ((Integer) value).longValue();
                } else if (value instanceof String) {
                    return Long.parseLong((String) value);
                }
            } catch (NoSuchFieldException e) {
                java.lang.reflect.Method getProductId = clazz.getMethod("getProductId");
                Object value = getProductId.invoke(obj);
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof Integer) {
                    return ((Integer) value).longValue();
                } else if (value instanceof String) {
                    return Long.parseLong((String) value);
                }
            }
        } catch (Exception e) {
            log.debug("无法从对象中提取productId: {}", obj.getClass().getName());
        }
        
        return null;
    }

    private Long extractIdFromObject(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            Class<?> clazz = obj.getClass();
            
            try {
                java.lang.reflect.Field idField = clazz.getDeclaredField("id");
                idField.setAccessible(true);
                Object value = idField.get(obj);
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof Integer) {
                    return ((Integer) value).longValue();
                }
            } catch (NoSuchFieldException e) {
                java.lang.reflect.Method getId = clazz.getMethod("getId");
                Object value = getId.invoke(obj);
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof Integer) {
                    return ((Integer) value).longValue();
                }
            }
        } catch (Exception e) {
            log.debug("无法从对象中提取id: {}", obj.getClass().getName());
        }
        
        return null;
    }

    private List<Long> getProductIdsByOrderId(Long orderId) {
        List<Long> productIds = new ArrayList<>();
        
        try {
            if (applicationContext != null) {
                try {
                    Object mapper = applicationContext.getBean("orderItemsMapper");
                    if (mapper != null) {
                        java.lang.reflect.Method byOrderIdMethod = mapper.getClass().getMethod("byOrderId", Long.class);
                        Object result = byOrderIdMethod.invoke(mapper, orderId);
                        
                        if (result instanceof List) {
                            List<?> items = (List<?>) result;
                            for (Object item : items) {
                                Long productId = extractProductIdFromObject(item);
                                if (productId != null) {
                                    productIds.add(productId);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("无法通过OrderItemsMapper获取商品ID: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("获取订单商品ID失败", e);
        }
        
        return productIds;
    }

    private void saveOperationLog(Long userId, int operateType, List<Long> productIds) {
        try {
            UserOperationLogPO logPO = new UserOperationLogPO();
            logPO.setUserId(userId);
            logPO.setOperateType(operateType);
            logPO.setOperateData(objectMapper.writeValueAsString(productIds));
            logPO.setCreateTime(LocalDateTime.now());
            
            userOperationLogMapper.insert(logPO);
            log.info("调用添加操作日志aop，添加数据：userId={}, operateType={}, productIds={}", userId, operateType, productIds);
        } catch (Exception e) {
            log.error("保存用户操作日志失败", e);
        }
    }
}

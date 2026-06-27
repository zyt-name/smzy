package com.clou.gatewaydemo.exception;

import com.clou.gatewaydemo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.clou.common.constant.code.errorCode;

/**
 * @author LENOVO
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 自定义异常处理方法
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleException(BusinessException e) {
        return Result.error(e.getMessage()).setCode(e.getCode()); // 需要在Result类中添加setCode方法
    }

    // 处理其他所有未定义的异常
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        // 在控制台/日志中打印详细异常信息，方便开发排查
        log.error("捕获到未定义异常: ", e);
        // 对外统一返回“未知异常”
        return Result.error("未知异常").setCode(errorCode);
    }

}

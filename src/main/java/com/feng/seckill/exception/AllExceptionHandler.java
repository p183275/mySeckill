package com.feng.seckill.exception;

import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.exception.entity.SQLDuplicateException;
import com.feng.seckill.exception.entity.TokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author : pcf
 * @date : 2022/1/15 18:51
 * 异常处理类
 */
@RestControllerAdvice
public class AllExceptionHandler {

    @ExceptionHandler(value = SQLDuplicateException.class)
    public CommonResult<String> resolve(SQLDuplicateException e){
        e.printStackTrace();
        return new CommonResult<>(441, e.getMessage());
    }

    @ExceptionHandler(value = TokenException.class)
    public CommonResult<String> resultTokenError(TokenException exception){
        exception.printStackTrace();
        return new CommonResult<>(444, "请重新登录!", exception.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public CommonResult<String> resultAllError(Exception exception){
        exception.printStackTrace();
        return new CommonResult<>(400, "失败", exception.getMessage());
    }

}

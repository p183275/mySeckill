package com.feng.seckill.exception;

import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.exception.entity.SQLDuplicateException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author : pcf
 * @date : 2022/1/23 17:39
 * SQL 的错误
 */
@RestControllerAdvice
public class SelfSQLExceptionHandler {

    @ExceptionHandler(value = SQLDuplicateException.class)
    public CommonResult<String> resolve(SQLDuplicateException e){
        e.printStackTrace();
        return new CommonResult<>(441, e.getMessage());
    }

}

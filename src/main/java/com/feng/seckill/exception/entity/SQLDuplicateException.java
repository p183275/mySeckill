package com.feng.seckill.exception.entity;

/**
 * @author : pcf
 * @date : 2022/1/23 17:37
 * SQL重复添加异常
 */
public class SQLDuplicateException extends RuntimeException{

    public SQLDuplicateException() {
        super();
    }

    public SQLDuplicateException(String message) {
        super(message);
    }

    public SQLDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLDuplicateException(Throwable cause) {
        super(cause);
    }

    protected SQLDuplicateException(String message, Throwable cause,
                             boolean enableSuppression,
                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

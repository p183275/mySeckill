package com.feng.seckill.exception.entity;

/**
 * @author : pcf
 * @date : 2022/1/21 16:17
 * token 异常类
 */
public class TokenException extends RuntimeException{
    public TokenException(){
        super("token验签错误，或已过期，请重新登录");
    }
}

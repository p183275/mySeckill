package com.feng.seckill.entitys.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author : pcf
 * @date : 2022/1/15 16:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommonResult<T>{

    // 返回代码
    private Integer code;

    // 返回的消息
    private String message;

    // 返回的数据
    private T data;

    public CommonResult(Integer code, String message){

        this(code, message, null);
    }

}

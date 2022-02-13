package com.feng.seckill.entitys.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "json格式统一封装类")
public class CommonResult<T>{

    @ApiModelProperty(value = "返回码", example = "200")
    private Integer code;

    @ApiModelProperty(value = "消息", example = "成功")
    private String message;

    @ApiModelProperty(value = "数据体", example = "泛型")
    private T data;

    public CommonResult(Integer code, String message){

        this(code, message, null);
    }

}

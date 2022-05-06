package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : pcf
 * @date : 2022/3/28 22:01
 */
@ApiModel(value = "支付封装类")
@Data
public class PayVO {

    @ApiModelProperty(value = "商品id", example = "1")
    private Long productId;
    @ApiModelProperty(value = "银行卡", example = "21312312")
    private String accountNumber;
    @ApiModelProperty(value = "支付密码", example = "123456")
    private String payPassword;

}

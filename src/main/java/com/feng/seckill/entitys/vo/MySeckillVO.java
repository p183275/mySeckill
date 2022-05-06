package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author : pcf
 * @date : 2022/1/21 17:10
 */
@Data
@ApiModel(value = "秒杀信息封装")
public class MySeckillVO {

    @ApiModelProperty(value = "产品id", example = "1")
    private Long productId;
    @ApiModelProperty(value = "产品名称",example = "产品1")
    private String productName;
//    @ApiModelProperty(value = "银行卡号", example = "12312412412412")
//    private String accountNum;
//    @ApiModelProperty(value = "支付密码", example = "123")
//    private  String payPassword;
}

package com.feng.seckill.entitys.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/3/7 19:01
 */
@Data
@ApiModel(value = "账户信息")
public class AccountVO {

    @ApiModelProperty(value = "账户 id", example = "1")
    private Long accountId;
    @ApiModelProperty(value = "银行卡号", example = "1231243543")
    private String accountNum;
    @JsonIgnore
    @ApiModelProperty(value = "支付密码", example = "123456")
    private String payPassword;
    @ApiModelProperty(value = "余额", example = "100.12")
    private BigDecimal balance;
    @ApiModelProperty(value = "创建日期", example = "2001-09-23")
    private Date createDate;

}

package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/20 21:47
 */
@Data
@TableName(value = "t_seckill_result_success")
//@TableName(value = "t_seckill_result")
@ApiModel(value = "秒杀结果封装类")
public class SeckillResultPO {

    @ApiModelProperty(value = "id", example = "1")
    @TableId
    private Long resultId;
    @ApiModelProperty(value = "订单id", example = "12345677723")
    private String orderId;
    @ApiModelProperty(value = "产品id", example = "1")
    private Long productId;
    @ApiModelProperty(value = "产品名称", example = "产品1")
    private String productName;
    @ApiModelProperty(value = "产品价格", example = "1000")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "用户姓名id", example = "1")
    private String userName;
    @ApiModelProperty(value = "支付状态 0-未支付 1-支付", example = "0")
    private String payStatus;
    @JsonIgnore
    @TableLogic
    private String showStatus;
    @ApiModelProperty(value = "创建时间", example = "2022-01-17 16:05:18")
    private Date createDate;

}

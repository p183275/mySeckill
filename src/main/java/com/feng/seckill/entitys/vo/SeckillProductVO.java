package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/16 15:56
 */
@Data
@ApiModel(value = "产品封装类")
public class SeckillProductVO {

    @ApiModelProperty(value = "产品id", example = "1")
    private Long productId; // 主键
    @ApiModelProperty(value = "产品名称", example = "产品1")
    private String productName;
    @ApiModelProperty(value = "活动链接", example = "http://域名/连接")
    private String url;
    @ApiModelProperty(value = "活动图片介绍", example = "连接", notes = "做图片上传备用，可以不写")
    private String photoUrl;
    @ApiModelProperty(value = "产品介绍", example = "介绍")
    private String productComment;
    @ApiModelProperty(value = "产品数量", example = "500")
    private Integer productNumber;
    @ApiModelProperty(value = "产品价格", example = "10000")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "产品是否展示在前台 0-展示 1-不展示", example = "0")
    private String showStatus;
    @ApiModelProperty(value = "产品净值", example = "4.2354")
    private BigDecimal worth;
    @ApiModelProperty(value = "产品状态（0-未开始 1-正在进行 2-已结束）")
    private String productStatus;
    @ApiModelProperty(value = "活动开始时间")
    private Date beginTime;
    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;
    @ApiModelProperty(value = "活动创建时间")
    private Date createDate;

}

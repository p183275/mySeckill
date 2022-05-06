package com.feng.seckill.entitys.vo;

import cn.hutool.core.text.replacer.StrReplacer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/16 16:40
 */
@Data
@ApiModel(value = "添加产品的封装类")
public class AddSeckillProductVO {

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
    @ApiModelProperty(value = "产品是否展示在前台 0-展示 1-不展示", example = "0", notes = "0-展示 1-不展示")
    private String showStatus;
    @ApiModelProperty(value = "产品净值", example = "4.2354")
    private BigDecimal worth;
    @ApiModelProperty(value = "产品价格", example = "10000")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "活动开始时间")
    private Date beginTime;
    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;

}

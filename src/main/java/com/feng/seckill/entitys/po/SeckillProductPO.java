package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/16 15:33
 */
@Data
@TableName("t_seckill_product")
public class SeckillProductPO {

    @TableId
    private Long productId; // 主键
    private String productName; // 产品名称
    private String url; // 活动链接
    private String photoUrl; // 活动图片介绍
    private String productComment; // 产品介绍
    private Integer productNumber; // 产品数量
    private Integer productPrice; // 产品价格
    private String productStatus; // 产品状态（未开始 正在进行 已结束）
    private BigDecimal worth; // 产品净值
    @TableLogic
    private String deleteStatus;
    private String showStatus;
    private Date beginTime; // 开始时间
    private Date endTime; // 结束时间
    private Date createDate; // 创建日期
}

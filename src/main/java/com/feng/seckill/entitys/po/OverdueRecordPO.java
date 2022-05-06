package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/2/14 20:26
 */
@ApiModel
@Data
@TableName(value = "t_user_overdue")
public class OverdueRecordPO {

    @ApiModelProperty(value = "记录id", example = "1")
    @TableId
    private Long recordId;
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "用户姓名", example = "张三")
    private String userName;
    @ApiModelProperty(value = "逾期金额", example = "199.01")
    private BigDecimal overMoney;
    @ApiModelProperty(value = "开始日期", example = "2022-02-13 18:50:56")
    private Date beginTime;
    @ApiModelProperty(value = "结束日期", example = "2022-02-13 18:50:56")
    private Date endTime;
}

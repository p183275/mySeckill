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
 * @date : 2022/2/15 21:52
 */
@ApiModel
@Data
@TableName("t_user_credit")
public class CreditPO {

    @ApiModelProperty(value = "记录id", example = "1")
    @TableId
    private Long recordId;
    @ApiModelProperty(value = "被执行人姓名id")
    private Long userId;
    @ApiModelProperty(value = "被执行人姓名")
    private String userName;
    @ApiModelProperty(value = "被执行人状态", example = "0-未生效 1-生效中")
    private String status;
    @ApiModelProperty(value = "被执行金额", example = "100.01")
    private BigDecimal money;
    @ApiModelProperty(value = "开始时间", example = "2022-02-13 18:50:56")
    private Date beginTime;
    @ApiModelProperty(value = "结束时间", example = "2022-02-13 18:50:56")
    private Date endTime;

}

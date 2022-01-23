package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/20 20:52
 */
@Data
@ApiModel(value = "用户破坏规则信息封装")
public class BreakRuleVO {

    @ApiModelProperty(value = "主键")
    private Long breakId;
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "用户姓名", example = "哈哈哈")
    private String userName;
    @ApiModelProperty(value = "规则id", example = "1")
    private Long ruleId;
    @ApiModelProperty(value = "规则名称")
    private String ruleName;
    @ApiModelProperty(value = "状态 0-失效 1-生效", example = "0")
    private String status;
    @ApiModelProperty(value = "开始时间")
    private Date beginTime;
}

package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : pcf
 * @date : 2022/1/17 15:30
 */
@Data
@ApiModel
public class SeckillRuleVO {

    @ApiModelProperty(value = "主键", example = "1")
    private Long ruleId;
    @ApiModelProperty(value = "规则名称", example = "个人状态异常")
    private String ruleName;
    @ApiModelProperty(value = "规则描述", example = "解释此规则")
    private String ruleContent;
    @ApiModelProperty(value = "适用对象(角色id) 0-管理员 1-个体客户 2-企业客户", example = "1")
    private Long ruleRole;
    @ApiModelProperty(value = "风险控制 0-拒绝 1-接收", example = "0")
    private String control;
    @ApiModelProperty(value = "规则状态 0-失效 1-生效")
    private String ruleStatus;

}

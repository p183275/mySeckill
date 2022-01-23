package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/17 14:58
 */
@TableName("t_seckill_rule")
@Data
public class SeckillRulePO {

    @TableId
    private Long ruleId; //主键
    private String ruleName; // 规则名称
    private String ruleContent; // 规则描述
    private Long ruleRole; // 使用对象（角色id）
    private String control; // 风险控制 0-拒绝 1-接收
    private String ruleStatus; // 规则状态 失效 生效
    private Date createDate; // 创建日期

}

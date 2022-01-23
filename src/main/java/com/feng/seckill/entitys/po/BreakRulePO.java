package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/20 19:45
 */
@Data
@TableName(value = "t_user_break_rule")
public class BreakRulePO {

    @TableId
    private Long breakId; // 主键
    private Long userId; // 用户id
    private String userName; // 用户姓名
    private Long ruleId; // 规则id
    private String ruleName; // 规则名称
    private String status; // 状态 0-失效 1-生效
    private Date beginTime; // 开始时间

}

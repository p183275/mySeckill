package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/2/14 20:26
 */
@Data
@TableName(value = "t_user_overdue")
public class OverdueRecordPO {

    @TableId
    private Long recordId;
    private Long userId;
    private String userName;
    private BigDecimal overMoney;
    private Date beginTime;
    private Date endTime;
}

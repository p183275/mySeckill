package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/2/15 21:52
 */
@Data
@TableName("t_user_credit")
public class CreditPO {

    @TableId
    private Long creditId;
    private Long userId;
    private String userName;
    private String status;
    private BigDecimal money;
    private Date beginTime;
    private Date endTime;

}

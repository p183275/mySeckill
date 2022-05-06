package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/4/8 21:01
 */
@TableName(value = "t_bank_info")
@Data
public class BankInfoPO {

    @TableId
    private Integer bankId;
    private String bankName;
    private BigDecimal bankAccount;
    private Date createDate;
}

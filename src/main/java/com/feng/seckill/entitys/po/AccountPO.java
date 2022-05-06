package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/3/7 18:45
 */
@Data
@TableName(value = "t_user_account")
public class AccountPO {

    @TableId
    private Long accountId; // id
    private String accountNum; // 银行卡号
    private String payPassword; // 支付密码
    private Long userId; // 用户 id
    private BigDecimal balance; // 余额
    private Date createDate; // 创建日期

}

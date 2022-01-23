package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/20 21:47
 */
@Data
@TableName(value = "t_seckill_result_success_user")
public class SeckillResultPO {

    @TableId
    private Long resultId;
    private Long productId;
    private String productName;
    private Long userId;
    private String userName;
    @TableLogic
    private String showStatus;
    private Date createDate;

}

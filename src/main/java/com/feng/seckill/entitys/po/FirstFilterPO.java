package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/20 21:27
 */
@TableName(value = "t_seckill_result_first_filter")
@Data
public class FirstFilterPO {

    @TableId
    private Long filterId; // 主键
    private Long userId; // 用户id
    private String userName; // 用户姓名
    private String passStatus; // 0-未通过 1-通过
    private Date createDate; // 创建时间

    public FirstFilterPO(Long userId, String userName, String passStatus){
        this.userId = userId;
        this.userName = userName;
        this.passStatus = passStatus;
    }

}

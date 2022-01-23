package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/15 16:05
 */
@Data
@TableName(value = "t_user")
public class UserPO {

    @TableId
    private Long userId; // 主键
    private String name; // 姓名
    private Integer age; // 年龄
    private String gender; // 性别
    private String address; // 地址
    private String loginAccount; // 登录账号
    @JsonIgnore
    private String password; // 登录密码
    private String phoneNumber; // 电话号码
    private Long roleId; // 身份
    private String roleName; // 角色名称
    private String idCard; // 身份证
    @TableLogic
    private String showStatus; // 逻辑删除
    private Date createDate; // 创建日期

}

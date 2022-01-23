package com.feng.seckill.entitys.vo;

import lombok.Data;

/**
 * @author : pcf
 * @date : 2022/1/16 13:30
 */
@Data
public class UserLoginVO {

    private String loginAccount; // 登录账号
    private String password; // 登录密码
    private String phoneNumber; // 电话号码
    private String checkCode; // 验证码

}

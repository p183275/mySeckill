package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : pcf
 * @date : 2022/1/16 13:33
 */
@Data
@ApiModel(value = "用户注册封装类")
public class UserRegisterVO {

    @ApiModelProperty(value = "姓名", example = "哈哈哈")
    private String name;
    @ApiModelProperty(value = "年龄", example = "1")
    private Integer age;
    @ApiModelProperty(value = "地址", example = "斗罗大陆")
    private String address;
    @ApiModelProperty(value = "登录账号", example = "admin")
    private String loginAccount;
    @ApiModelProperty(value = "密码", example = "********")
    private String password;
    @ApiModelProperty(value = "电话号码", example = "1……")
    private String phoneNumber;
    @ApiModelProperty(value = "验证码", example = "1234")
    private String checkCode;
    @ApiModelProperty(value = "身份证", example = "420……")
    private String idCard;

}

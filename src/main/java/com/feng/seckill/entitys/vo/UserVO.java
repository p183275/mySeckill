package com.feng.seckill.entitys.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/15 17:04
 */
@Data
@ApiModel(value = "用户信息封装类")
public class UserVO {

    @ApiModelProperty(value = "主键", example = "1")
    private Long userId;
    @ApiModelProperty(value = "姓名", example = "Jack")
    private String name;
    @ApiModelProperty(value = "年龄", example = "20")
    private Integer age;
    @ApiModelProperty(value = "性别", example = "0 - 女 1 - 男")
    private String gender;
    @ApiModelProperty(value = "年龄", example = "20")
    private String address;
    @ApiModelProperty(value = "年龄", example = "20")
    private String loginAccount;
    @ApiModelProperty(value = "登录密码", example = "haha")
    @JsonIgnore
    private String password;
    @ApiModelProperty(value = "电话号码", example = "1....")
    private String phoneNumber;
    @ApiModelProperty(value = "角色Id", example = "2")
    private Long roleId;
    @ApiModelProperty(value = "角色名称", example = "个人客户")
    private String roleName;
    @ApiModelProperty(value = "身份证", example = "20")
    private String idCard;
    @ApiModelProperty(value = "创建日期", example = "2001-09-23")
    private Date createDate;
    @ApiModelProperty(value = "是否有资格参加活动", example = "false")
    private boolean hasPermission;
}

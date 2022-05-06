package com.feng.seckill.entitys.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/3/7 18:58
 */
@Data
@ApiModel(value = "用户的所有信息以及账户信息")
public class UserInfoAndAccountVO {

    @ApiModelProperty(value = "主键", example = "1")
    private Long userId;
    @ApiModelProperty(value = "姓名", example = "Jack")
    private String name;
    @ApiModelProperty(value = "年龄", example = "20")
    private Integer age;
    @ApiModelProperty(value = "性别", example = "0 - 女 1 - 男")
    private String gender;
    @ApiModelProperty(value = "地址", example = "20")
    private String address;
    @ApiModelProperty(value = "账号", example = "20")
    private String loginAccount;
    @ApiModelProperty(value = "电话号码", example = "1....")
    private String phoneNumber;
    @ApiModelProperty(value = "角色Id", example = "2")
    private Long roleId;
    @ApiModelProperty(value = "角色名称", example = "个人客户")
    private String roleName;
    @ApiModelProperty(value = "身份证", example = "20")
    private String idCard;
    @ApiModelProperty(value = "0-无业/失业 1-正在就业", example = "0")
    private String workStatus;
    @ApiModelProperty(value = "创建日期", example = "2001-09-23")
    private Date createDate;
    @ApiModelProperty(value = "账户信息")
    private AccountVO accountVO;

}

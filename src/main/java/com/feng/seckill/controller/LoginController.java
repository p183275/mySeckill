package com.feng.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.UserLoginVO;
import com.feng.seckill.entitys.vo.UserRegisterVO;
import com.feng.seckill.service.UserLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/1/15 16:23
 */
@RestController
@RequestMapping(value = "/user")
@Api(tags = "登录：登录管理")
public class LoginController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/get/check/code")
    @ApiOperation(value = "用户发送短信拿到验证码", httpMethod = "POST")
    public CommonResult<String> getCheckCode(
            @ApiParam(value = "传入电话号码", name = "phoneNumber")
               @RequestBody String phoneNumber){

        userLoginService.getCheckCode(phoneNumber);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/register")
    @ApiOperation(value = "用户注册", httpMethod = "POST")
    public CommonResult<String> registerUser(
            @ApiParam(value = "所有参数", name = "vo")
                @RequestBody UserRegisterVO vo){

        userLoginService.registerUser(vo);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/admin/login")
    @ApiOperation(value = "管理员登录", httpMethod = "POST")
    public CommonResult<Map<String, String>> adminLogin(
            @ApiParam(value = "账号密码登录所需要的参数")
                @RequestBody UserLoginVO loginVO){

        Map<String, String> map = userLoginService.adminLogin(loginVO);
        return new CommonResult<>(200, "成功", map);
    }

}

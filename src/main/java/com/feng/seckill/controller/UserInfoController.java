package com.feng.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.SeckillProductVO;
import com.feng.seckill.entitys.vo.UserVO;
import com.feng.seckill.service.UserInfoService;
import com.feng.seckill.service.UserLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/18 21:51
 */
@RestController
@RequestMapping(value = "/user")
@Api(tags = "后台：用户信息管理")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping(value = "/get/user/info")
    @ApiOperation(value = "拿到用户信息", httpMethod = "GET")
    public CommonResult<Page<UserPO>> getAllUser(
            @ApiParam(value = "当前页码", name = "current", example = "1", defaultValue = "1")
                @RequestParam(value = "current", required = false, defaultValue = "1") Long current,
            @ApiParam(value = "每页最大数据量", name = "maxLimit", example = "1", defaultValue = "10")
                @RequestParam(value = "maxLimit", required = false, defaultValue = "10") Long maxLimit,
            @ApiParam(value = "关键字", name = "key")
                @RequestParam(value = "key", required = false) String key,
            @ApiParam(value = "性别")
                @RequestParam(value = "gender", required = false) String gender){

        Page<UserPO> userPOPage = userInfoService.queryPage(key, gender, new HelpPage(current, maxLimit));
        return new CommonResult<>(200, "成功", userPOPage);
    }

    @PostMapping(value = "/delete/by/id")
    @ApiOperation(value = "删除用户", httpMethod = "POST")
    public CommonResult<String> deleteUsers(
            @ApiParam(value = "用户id")
            @RequestBody List<Long> userIdList){

        userInfoService.deleteUserByIdList(userIdList);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/update/info")
    @ApiOperation(value = "修改用户信息", httpMethod = "POST", notes = "选几个可以自由修改的属性")
    public CommonResult<String> updateUser(
            @ApiParam(value = "用户属性", name = "vo")
            @RequestBody UserVO vo){

        userInfoService.updateUser(vo);
        return new CommonResult<>(200, "成功");
    }

}

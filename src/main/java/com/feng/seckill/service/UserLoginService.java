package com.feng.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.UserLoginVO;
import com.feng.seckill.entitys.vo.UserRegisterVO;

import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/1/15 16:26
 */
public interface UserLoginService extends IService<UserPO> {

    /**
     * 用户注册
     * @param vo 用户信息封装类
     */
    void registerUser(UserRegisterVO vo);

    /**
     * 拿到验证码
     * @param phoneNumber 电话号码
     */
    void getCheckCode(String phoneNumber);

    /**
     * 管理员登录
     * @param loginVO 登录信息封装类欸
     * @return token
     */
    Map<String, String> adminLogin(UserLoginVO loginVO);

}

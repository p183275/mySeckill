package com.feng.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.UserLoginVO;
import com.feng.seckill.entitys.vo.UserRegisterVO;
import com.feng.seckill.entitys.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * 拿到当前登录用户的所有请求
     * @param request 请求
     * @return 用户信息
     */
    UserVO getLoginUserInfo(HttpServletRequest request);
}

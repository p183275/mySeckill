package com.feng.seckill.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.UserInfoAndAccountVO;
import com.feng.seckill.entitys.vo.UserVO;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/18 21:48
 */
public interface UserInfoService extends IService<UserPO> {

    /**
     * 分页查出所有用户数据并且可以进行检索
     * @param key 关键字
     * @param gender 性别
     * @param helpPage 分页数据
     * return 分页数据信息
     */
    Page<UserPO> queryPage(String key, String gender, HelpPage helpPage);

    /**
     * 根据用户id删除用户--逻辑删除
     * @param userIdList 用户id集合
     */
    void deleteUserByIdList(List<Long> userIdList);

    /**
     * 修改用户信息
     * @param vo 用户信息封装类
     */
    void updateUser(UserVO vo);

    /**
     * 通过 userId 查找用户信息
     * @param userId is
     * @return 用户及账户信息
     */
    UserInfoAndAccountVO getAllInfo(Long userId);
}

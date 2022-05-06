package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.po.RolePO;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.UserInfoAndAccountVO;
import com.feng.seckill.entitys.vo.UserVO;
import com.feng.seckill.mapper.RoleMapper;
import com.feng.seckill.mapper.UserInfoMapper;
import com.feng.seckill.service.RoleService;
import com.feng.seckill.service.UserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/18 21:47
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserPO> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * @param key 关键字
     * @param gender 性别
     * @param helpPage 分页数据
     * return 分页数据信息
     */
    @Override
    public Page<UserPO> queryPage(String key, String gender, HelpPage helpPage) {

        // 创建对象 复制数据
        Page<UserPO> userPOPage = new Page<>();
        BeanUtils.copyProperties(helpPage, userPOPage);

        // 创建包装器
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();

        if (gender != null && gender.length() > 0)
            queryWrapper.eq("gender", gender);

        // 添加索引验证
        if (key != null && key.length() >0){
            queryWrapper.and(wrapper -> {
                wrapper.eq("user_id", key)
                        .or().like("name", key)
                        .or().like("address", key);
            });
        }

        return userInfoMapper.selectPage(userPOPage, queryWrapper);
    }

    /**
     * 根据用户id删除用户--逻辑删除
     * @param userIdList 用户id集合
     */
    @Override
    public void deleteUserByIdList(List<Long> userIdList) {
        if (userIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 删除
        userInfoMapper.deleteBatchIds(userIdList);
    }

    /**
     * 修改用户信息
     * @param vo 用户信息封装类
     */
    @Override
    public void updateUser(UserVO vo) {

        // 判断信息是否为空
        if (vo == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象 复制属性
        UserPO userPO = new UserPO();
        BeanUtils.copyProperties(vo, userPO);

        // 判断角色id是否为空
        if (userPO.getRoleId() != null){
            RolePO rolePO = roleMapper.selectById(userPO.getRoleId());
            // 放入角色属性
            userPO.setRoleName(rolePO.getRoleName());
        }

        // 修改
        userInfoMapper.updateById(userPO);
    }

    /**
     * 通过 userId 查找用户信息
     * @param userId is
     * @return 用户及账户信息
     */
    @Override
    public UserInfoAndAccountVO getAllInfo(Long userId) {

        return userInfoMapper.getUserInfoAndAccountVO(userId);
    }
}

package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feng.seckill.entitys.constant.FirstFilterConstant;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.UserVO;
import com.feng.seckill.mapper.UserLoginMapper;
import com.feng.seckill.security.SecurityUser;
import com.feng.seckill.service.FirstFilterService;
import com.feng.seckill.service.SeckillRuleService;
import com.feng.seckill.service.rabbitMq.WriteDBProducerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.conn.Wire;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/15 17:37
 */
@Slf4j
@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserLoginMapper userLoginMapper;
    @Autowired
    private SeckillRuleService seckillRuleService;
    @Autowired
    private FirstFilterService firstFilterService;
    @Autowired
    private WriteDBProducerService writeDBProducerService;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String loginAccount) throws UsernameNotFoundException {

        // 从数据库中查出数据
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_account", loginAccount);
        UserPO userPO = userLoginMapper.selectOne(queryWrapper);

        // 复制数据
        SecurityUser securityUser = new SecurityUser();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userPO, securityUser);
        BeanUtils.copyProperties(userPO, userVO);

        // 查询用户是否有资格
        boolean permission = seckillRuleService.getPermissionByUserId(userPO);
        userVO.setHasPermission(permission);

        // TODO 开发期间先关闭
//         将初筛信息放入数据库
        String passStatus = permission ? FirstFilterConstant.FilterStatus.PASS.getCode()
                : FirstFilterConstant.FilterStatus.NOT_PASS.getCode();
        writeDBProducerService.writeFirstFilter(userVO.getUserId(), userVO.getName(), passStatus);

//        firstFilterService.addRecordsByUser(userVO.getUserId(), userVO.getName(), passStatus);

        // 放置数据
        securityUser.setLoginUser(userVO);

        return securityUser;
    }

}

package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.po.RolePO;
import com.feng.seckill.mapper.RoleMapper;
import com.feng.seckill.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @author : pcf
 * @date : 2022/1/23 21:39
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RolePO> implements RoleService {
}

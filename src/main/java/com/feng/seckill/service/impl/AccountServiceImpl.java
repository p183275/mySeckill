package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.po.AccountPO;
import com.feng.seckill.mapper.AccountMapper;
import com.feng.seckill.service.AccountService;
import org.springframework.stereotype.Service;

/**
 * @author : pcf
 * @date : 2022/3/7 19:36
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountPO> implements AccountService {
}

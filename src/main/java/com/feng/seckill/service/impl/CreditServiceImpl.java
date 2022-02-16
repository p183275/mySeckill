package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.po.CreditPO;
import com.feng.seckill.mapper.CreditMapper;
import com.feng.seckill.service.CreditService;
import org.springframework.stereotype.Service;

/**
 * @author : pcf
 * @date : 2022/2/15 21:55
 */
@Service
public class CreditServiceImpl extends ServiceImpl<CreditMapper, CreditPO> implements CreditService {
}

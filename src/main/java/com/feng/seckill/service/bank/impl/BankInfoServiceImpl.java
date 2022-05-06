package com.feng.seckill.service.bank.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.po.BankInfoPO;
import com.feng.seckill.mapper.BankInfoMapper;
import com.feng.seckill.service.bank.BankInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author : pcf
 * @date : 2022/4/9 16:31
 */
@Service
public class BankInfoServiceImpl extends ServiceImpl<BankInfoMapper, BankInfoPO>
        implements BankInfoService {

    @Autowired
    private BankInfoMapper bankInfoMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;


    // 获取银行信息
    @Override
    public BankInfoPO getBankInfo() {

        // 将redis中的数据写入
        String s = redisTemplate.opsForValue().get(RedisConstant.BANK_ACCOUNT);

        if (s != null){
            bankInfoMapper.updateBankAccount(new BigDecimal(s));
        }

        return bankInfoMapper.selectById(1);
    }
}

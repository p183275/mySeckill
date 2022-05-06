package com.feng.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.seckill.entitys.po.BankInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author : pcf
 * @date : 2022/4/8 21:05
 */
@Mapper
public interface BankInfoMapper extends BaseMapper<BankInfoPO> {

    void incBankAccount(@Param("money") BigDecimal money);

    void updateBankAccount(@Param("bankAccount") BigDecimal bankAccount);
}

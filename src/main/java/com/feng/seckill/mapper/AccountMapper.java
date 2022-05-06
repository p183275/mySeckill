package com.feng.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.seckill.entitys.po.AccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author : pcf
 * @date : 2022/3/7 19:34
 */
@Mapper
public interface AccountMapper extends BaseMapper<AccountPO> {

    boolean decBalance(@Param("accountId") Long accountId, @Param("price") BigDecimal price, @Param("oldBalance") BigDecimal oldBalance);

    void incBalance(@Param("accountId") Long accountId, @Param("price") BigDecimal price);
}

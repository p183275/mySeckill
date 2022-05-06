package com.feng.seckill.service.bank;

import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.BankInfoPO;

/**
 * @author : pcf
 * @date : 2022/4/9 16:30
 */
public interface BankInfoService extends IService<BankInfoPO> {

    BankInfoPO getBankInfo();

}

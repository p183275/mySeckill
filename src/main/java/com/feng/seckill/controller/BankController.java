package com.feng.seckill.controller;

import com.feng.seckill.entitys.po.BankInfoPO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.service.bank.BankInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : pcf
 * @date : 2022/4/9 16:45
 */
@RestController
@Api(tags = "后台：银行信息")
@RequestMapping(value = "/bank")
public class BankController {

    @Autowired
    private BankInfoService bankInfoService;

    @GetMapping(value = "/get/info")
    @ApiOperation(value = "拿到银行信息", httpMethod = "GET")
    public CommonResult<BankInfoPO> getBankInfo(){
        BankInfoPO bankInfo = bankInfoService.getBankInfo();
        return new CommonResult<>(200, "成功", bankInfo);
    }


}

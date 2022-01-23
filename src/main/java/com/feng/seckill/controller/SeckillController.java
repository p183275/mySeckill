package com.feng.seckill.controller;

import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.po.SeckillRulePO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.MySeckillVO;
import com.feng.seckill.service.SeckillProductService;
import com.feng.seckill.service.SeckillRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/1/20 22:06
 */
@RestController
@RequestMapping(value = "/get/seckill")
@Api(tags = "秒杀系统API")
public class SeckillController {

    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private SeckillRuleService seckillRuleService;

    @GetMapping(value = "/show/productions")
    @ApiOperation(value = "向用户展示商品", httpMethod = "GET", notes = "请勿使用其中的产品数量，另有接口查询")
    public CommonResult<List<SeckillProductPO>> showProductions(){
        List<SeckillProductPO> seckillProductPOS = seckillProductService.showProductions();
        return new CommonResult<>(200, "成功", seckillProductPOS);
    }

    @GetMapping(value = "/show/rules")
    @ApiOperation(value = "秒杀活动规则", httpMethod = "GET")
    public CommonResult<List<SeckillRulePO>> showRules(){
        List<SeckillRulePO> seckillRulePOS = seckillRuleService.showRules();
        return new CommonResult<>(200, "成功", seckillRulePOS);
    }

    @GetMapping(value = "/show/product/number")
    @ApiOperation(value = "拿到产品数量", httpMethod = "GET", notes = "返回值{商品id + 数量}")
    public CommonResult<Map<String, String>> getProductNumberFromRedis(){
        Map<String, String> resMap = seckillProductService.getProductNumberFromRedis();
        return new CommonResult<>(200, "成功", resMap);
    }

    // 用户参与秒杀
    @PostMapping(value = "/do/{dynamicUrl1}/{dynamicUrl2}")
    @ApiOperation(value = "抢购", httpMethod = "POST", notes = "dynamicUrl为动态生成的链接")
    public CommonResult<String> userSeckill(
            @ApiParam(value = "动态链接，从链接中获取", name = "dynamicUrl")
                @PathVariable(value = "dynamicUrl1") String dynamicUrl1,
            @ApiParam(value = "动态链接，从链接中获取", name = "dynamicUrl")
                @PathVariable(value = "dynamicUrl2") String dynamicUrl2,
            @ApiParam(value = "秒杀信息封装", name = "mySeckillVO")
                @RequestBody MySeckillVO mySeckillVO){

        String result = seckillProductService.doSeckill(dynamicUrl1, dynamicUrl2, mySeckillVO);
        return new CommonResult<>(200, "成功", result);
    }

}

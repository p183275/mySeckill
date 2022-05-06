package com.feng.seckill.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.po.SeckillRulePO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.MySeckillVO;
import com.feng.seckill.entitys.vo.PayVO;
import com.feng.seckill.mapper.BankInfoMapper;
import com.feng.seckill.service.PersonalService;
import com.feng.seckill.service.SeckillProductService;
import com.feng.seckill.service.SeckillRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/1/20 22:06
 */
@RestController
@RequestMapping(value = "/get/seckill")
@Api(tags = "前台：秒杀系统API")
public class SeckillController {

    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private SeckillRuleService seckillRuleService;
    @Autowired
    private BankInfoMapper bankInfoMapper;
    @Autowired
    private PersonalService personalService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping(value = "/test")
    public void test(){
//        bankInfoMapper.incBankAccount(new BigDecimal(1000));
        redisTemplate.opsForValue().increment(RedisConstant.BANK_ACCOUNT, new BigDecimal(1000).doubleValue());
    }

    @GetMapping(value = "/create/user")
    public CommonResult<String> createUser(@RequestParam("number") Integer number){
        personalService.createUser(number);
        return new CommonResult<>(200, "成功");
    }

    @GetMapping(value = "/user/get/all/info")
    @ApiOperation(value = "用户端条件查询", httpMethod = "GET")
    public CommonResult<List<SeckillProductPO>> userGetProduct(
            @ApiParam(value = "商品状态 0-未开始 1-正在进行 2-已结束", defaultValue = "0", example = "0", name = "productStatus")
            @RequestParam(value = "productStatus", required = false) String productStatus,
            @ApiParam(value = "商品价格 ASC-升序 DESC-降序", defaultValue = "ASC", example = "ASC", name = "productPrice")
            @RequestParam(value = "productPrice", required = false) String productPrice,
            @ApiParam(value = "商品净值 ASC-升序 DESC-降序", defaultValue = "ASC", example = "ASC", name = "worth")
            @RequestParam(value = "worth", required = false) String worth,
            @ApiParam(value = "关键字", required = false, name = "key")
            @RequestParam(value = "key", required = false) String key) {

        List<SeckillProductPO> page = seckillProductService.queryPage(productStatus, productPrice, worth, key);
        return new CommonResult<>(200, "成功", page);
    }

    @GetMapping(value = "/dispay")
    @ApiOperation(value = "用户取消付款", httpMethod = "GET", notes = "用户取消支付")
    public CommonResult<String> dispay(
            @ApiParam(value = "商品id", name = "productId")
            @RequestParam("productId") Long productId,
            HttpServletRequest request){

        seckillProductService.disPay(request, productId);
        return new CommonResult<>(200, "取消成功！");
    }

    @PostMapping(value = "/pay")
    @ApiOperation(value = "用户付款", httpMethod = "POST", notes = "用户付款接口")
    public CommonResult<String> pay(
            @ApiParam(value = "支付封装类", name = "payVO")
            @RequestBody PayVO payVO,
            HttpServletRequest request){

        seckillProductService.pay(request, payVO);
        return new CommonResult<>(200, "支付成功");
    }

    @GetMapping(value = "/get/order")
    @ApiOperation(value = "根据商品id拿到用户订单", httpMethod = "GET", notes = "适用于秒杀完毕")
    public CommonResult<SeckillResultPO> getOrder(
            @ApiParam(value = "商品id", name = "productId")
                @RequestParam("productId") Long productId,
            HttpServletRequest request) {

        SeckillResultPO ordered = seckillProductService.getOrder(request, productId);
        return new CommonResult<>(200, "成功", ordered);
    }


    @GetMapping(value = "/check/order")
    @ApiOperation(value = "判断当前用户有无未消费订单", httpMethod = "GET", notes = "在用户")
    public CommonResult<SeckillResultPO> checkOrder(HttpServletRequest request) {
        SeckillResultPO ordered = seckillProductService.checkOrdered(request);
        return new CommonResult<>(200, "成功", ordered);
    }

    @GetMapping(value = "/show/productions")
    @ApiOperation(value = "向用户展示商品", httpMethod = "GET", notes = "请勿使用其中的产品数量，另有接口查询")
    public CommonResult<List<SeckillProductPO>> showProductions() {
        List<SeckillProductPO> seckillProductPOS = seckillProductService.showProductions();
        return new CommonResult<>(200, "成功", seckillProductPOS);
    }

    @GetMapping(value = "/show/rules")
    @ApiOperation(value = "秒杀活动规则", httpMethod = "GET")
    public CommonResult<List<SeckillRulePO>> showRules() {
        List<SeckillRulePO> seckillRulePOS = seckillRuleService.showRules();
        return new CommonResult<>(200, "成功", seckillRulePOS);
    }

    @GetMapping(value = "/show/product/number")
    @ApiOperation(value = "拿到产品数量", httpMethod = "GET", notes = "返回值{商品id + 数量}")
    public CommonResult<Map<String, String>> getProductNumberFromRedis() {
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
            @RequestBody MySeckillVO mySeckillVO,
            HttpServletRequest request) {

        String result = seckillProductService.doSeckill(dynamicUrl1, dynamicUrl2, mySeckillVO, request);
        return new CommonResult<>(200, "成功", result);
    }

}

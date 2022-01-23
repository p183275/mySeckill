package com.feng.seckill.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.seckill.entitys.po.SeckillRulePO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.SeckillRuleVO;
import com.feng.seckill.service.SeckillRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/17 15:03
 */
@RestController
@Api(tags = "后台：秒杀规则管理")
@RequestMapping(value = "/rule")
public class SeckillRuleController {

    @Autowired
    private SeckillRuleService seckillRuleService;

    @GetMapping(value = "/get/all/info")
    @ApiOperation(value = "拿到所有规则分页数据", httpMethod = "GET")
    public CommonResult<Page<SeckillRulePO>> getSeckillRulePages(
            @ApiParam(value = "当前页码", name = "current", example = "1", defaultValue = "1")
                @RequestParam(value = "current", required = false, defaultValue = "1") Long current,
            @ApiParam(value = "每页最大数据量", name = "maxLimit", example = "1", defaultValue = "10")
                @RequestParam(value = "maxLimit", required = false, defaultValue = "10") Long maxLimit){

        Page<SeckillRulePO> page = seckillRuleService.queryPage(new HelpPage(current, maxLimit));
        return new CommonResult<>(200, "成功", page);
    }

    @PostMapping(value = "/add/info")
    @ApiOperation(value = "增加规则", httpMethod = "POST")
    public CommonResult<String> addSeckillRule(
            @ApiParam(value = "规则封装类 不需要传ruleId 和 ruleStatus", name = "seckillRuleVO")
                @RequestBody SeckillRuleVO seckillRuleVO){

        seckillRuleService.addSeckillRule(seckillRuleVO);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/update/info")
    @ApiOperation(value = "修改规则", httpMethod = "POST")
    public CommonResult<String> updateSeckillRule(
            @ApiParam(value = "规则封装类", name = "seckillRuleVO")
            @RequestBody SeckillRuleVO seckillRuleVO){

        seckillRuleService.updateSeckillRule(seckillRuleVO);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/delete/by/id")
    @ApiOperation(value = "删除规则", httpMethod = "POST", notes = "注意可以批量删除")
    public CommonResult<String> deleteProduct(
            @ApiParam(value = "规则id", name = "ruleIdList")
                @RequestBody List<Long> ruleIdList){

        seckillRuleService.deleteSeckillRule(ruleIdList);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/add/activity/rules")
    @ApiOperation(value = "给秒杀活动添加规则", httpMethod = "POST", notes = "可以批量添加")
    public CommonResult<String> addActivityRules(
            @ApiParam(value = "规则id", name = "ruleIdList")
                @RequestBody List<Long> ruleIdList){

        seckillRuleService.addRulesToActivity(ruleIdList);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value="/test/permission")
    public CommonResult<Boolean> test(@RequestBody Long userId){
        boolean permission = seckillRuleService.getPermissionByUserId(userId);
        return new CommonResult<>(200, "成功", permission);
    }
}

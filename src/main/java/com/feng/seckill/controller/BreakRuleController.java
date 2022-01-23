package com.feng.seckill.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.seckill.entitys.po.BreakRulePO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.BreakRuleVO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.service.BreakRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 20:04
 */
@RestController
@Api(tags = "后台：违规信息管理")
@RequestMapping(value = "/break/rule")
public class BreakRuleController {

    @Autowired
    private BreakRuleService breakRuleService;

    @GetMapping(value = "/get/info")
    @ApiOperation(value = "检索违规信息管理", httpMethod = "GET")
    public CommonResult<Page<BreakRulePO>> getInfoByPage(
            @ApiParam(value = "当前页码", name = "current", example = "1", defaultValue = "1")
                @RequestParam(value = "current", required = false, defaultValue = "1") Long current,
            @ApiParam(value = "每页最大数据量", name = "maxLimit", example = "1", defaultValue = "10")
                @RequestParam(value = "maxLimit", required = false, defaultValue = "10") Long maxLimit,
            @ApiParam(value = "关键字", name = "key")
                @RequestParam(value = "key", required = false) String key,
            @ApiParam(value = "状态 0-失效 1-生效")
                @RequestParam(value = "status", required = false) String status){

        Page<BreakRulePO> page = breakRuleService.queryPage(key, status, new HelpPage(current, maxLimit));
        return new CommonResult<>(200, "成功", page);
    }

    @PostMapping(value = "/add/info")
    @ApiOperation(value = "增加记录", httpMethod = "POST")
    public CommonResult<String> addRecord(
            @ApiParam(value = "破坏规则信息封装", name = "breakRuleVO")
                @RequestBody BreakRuleVO breakRuleVO){

        breakRuleService.addBreakRuleRecord(breakRuleVO);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/update/info")
    @ApiOperation(value = "修改记录", httpMethod = "POST")
    public CommonResult<String> updateRecord(
            @ApiParam(value = "破坏规则信息封装", name = "breakRuleVO")
                @RequestBody BreakRuleVO breakRuleVO){

        breakRuleService.updateBreakRuleRecord(breakRuleVO);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/delete/info")
    @ApiOperation(value = "删除记录", httpMethod = "POST", notes = "可以批量删除")
    public CommonResult<String> deleteRecord(
            @ApiParam(value = "主键id", name = "breakIdList")
                @RequestBody List<Long> breakIdList){

        breakRuleService.deleteBreakRuleRecord(breakIdList);
        return new CommonResult<>(200, "成功");
    }

}

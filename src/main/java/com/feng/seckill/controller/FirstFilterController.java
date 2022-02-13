package com.feng.seckill.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.seckill.entitys.po.FirstFilterPO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.service.FirstFilterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 21:21
 */
@RestController
@RequestMapping(value = "/first/filter")
@Api(tags = "后台：初筛结果管理")
public class FirstFilterController {

    @Autowired
    private FirstFilterService firstFilterService;

    @GetMapping(value = "/get/info")
    @ApiOperation(value = "检索初筛结果", httpMethod = "GET")
    public CommonResult<Page<FirstFilterPO>> getInfoByPage(
            @ApiParam(value = "当前页码", name = "current", example = "1", defaultValue = "1")
                @RequestParam(value = "current", required = false, defaultValue = "1") Long current,
            @ApiParam(value = "每页最大数据量", name = "maxLimit", example = "1", defaultValue = "10")
                @RequestParam(value = "maxLimit", required = false, defaultValue = "10") Long maxLimit,
            @ApiParam(value = "关键字", name = "key")
                @RequestParam(value = "key", required = false) String key,
            @ApiParam(value = "状态 0-失效 1-生效", name = "passStatus")
                @RequestParam(value = "passStatus", required = false) String passStatus){

        Page<FirstFilterPO> page = firstFilterService.queryPage(key, passStatus, new HelpPage(current, maxLimit));
        return new CommonResult<>(200, "成功", page);
    }

    @PostMapping(value = "/delete/info")
    @ApiOperation(value = "删除记录", httpMethod = "POST", notes = "可以批量删除")
    public CommonResult<String> deleteRecord(
            @ApiParam(value = "主键id", name = "breakIdList")
                @RequestBody List<Long> filterIdList){

        firstFilterService.deleteRecords(filterIdList);
        return new CommonResult<>(200, "成功");
    }

}

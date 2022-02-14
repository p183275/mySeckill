package com.feng.seckill.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.seckill.entitys.po.FirstFilterPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.service.SeckillResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 21:20
 */
@RestController
@RequestMapping(value = "/seckill/result")
@Api(tags = "后台：秒杀结果管理")
public class SeckillResultController {

    @Autowired
    private SeckillResultService seckillResultService;

    @GetMapping(value = "/get/info")
    @ApiOperation(value = "检索秒杀结果", httpMethod = "GET")
    public CommonResult<Page<SeckillResultPO>> getInfoByPage(
            @ApiParam(value = "当前页码", name = "current", example = "1", defaultValue = "1")
                @RequestParam(value = "current", required = false, defaultValue = "1") Long current,
            @ApiParam(value = "每页数据量", name = "size", example = "1", defaultValue = "10")
                @RequestParam(value = "size", required = false, defaultValue = "10") Long size,
            @ApiParam(value = "关键字", name = "key")
                @RequestParam(value = "key", required = false) String key,
            @ApiParam(value = "用户id", name = "productId")
                @RequestParam(value = "productId", required = false) Long productId){

        Page<SeckillResultPO> page = seckillResultService.queryPage(key, productId, new HelpPage(current, size));
        return new CommonResult<>(200, "成功", page);
    }

    @PostMapping(value = "/delete/info")
    @ApiOperation(value = "删除记录", httpMethod = "POST", notes = "可以批量删除")
    public CommonResult<String> deleteRecord(
            @ApiParam(value = "主键id", name = "resultIdList")
                @RequestBody List<Long> resultIdList){

        seckillResultService.deleteRecords(resultIdList);
        return new CommonResult<>(200, "成功");
    }

}

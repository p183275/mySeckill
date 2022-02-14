package com.feng.seckill.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.AddSeckillProductVO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.RandomProductUrlVO;
import com.feng.seckill.entitys.vo.SeckillProductVO;
import com.feng.seckill.service.SeckillProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/16 15:41
 */
@RestController
@Api(tags = "后台：秒杀产品管理")
@RequestMapping(value = "/product")
public class SeckillProductController {

    @Autowired
    private SeckillProductService seckillProductService;

    @GetMapping(value = "/reflash/production/number")
    @ApiOperation(value = "刷新产品数量", httpMethod = "GET")
    public CommonResult<String> reflashProductionNumber(){

        seckillProductService.reflashProductionNumber();
        return new CommonResult<>(200, "成功");
    }

    @GetMapping(value = "/get/all/info")
    @ApiOperation(value = "拿到所有产品信息", httpMethod = "GET")
    public CommonResult<IPage<SeckillProductVO>> getAllProduct(
            @ApiParam(value = "当前页码", name = "current")
                @RequestParam(value = "current", required = false, defaultValue = "1") Long current,
            @ApiParam(value = "每页数据量", name = "size", example = "1", defaultValue = "10")
                @RequestParam(value = "size", required = false, defaultValue = "10") Long size){

        IPage<SeckillProductVO> page = seckillProductService.queryPage(new HelpPage(current, size));
        return new CommonResult<>(200, "成功", page);
    }

    @PostMapping(value = "/add/info")
    @ApiOperation(value = "添加产品", httpMethod = "POST")
    public CommonResult<String> addProduct(
            @ApiParam(value = "产品属性", name = "vo")
                @RequestBody AddSeckillProductVO vo){

        seckillProductService.addSeckillProduct(vo);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/update/info")
    @ApiOperation(value = "修改产品", httpMethod = "POST")
    public CommonResult<String> updateProduct(
            @ApiParam(value = "产品属性", name = "vo")
            @RequestBody SeckillProductVO vo){

        seckillProductService.updateSeckillProduct(vo);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/delete/by/id")
    @ApiOperation(value = "删除产品", httpMethod = "POST")
    public CommonResult<String> deleteProduct(
            @ApiParam(value = "产品id", name = "productIdList")
            @RequestBody List<Long> productIdList){

        seckillProductService.deleteSeckillProduct(productIdList);
        return new CommonResult<>(200, "成功");
    }

    @PostMapping(value = "/update/url")
    @ApiOperation(value = "点击按钮修改活动链接", httpMethod = "POST")
    public CommonResult<String> updateUrl(
            @ApiParam(value = "活动id", name = "productId")
            @RequestBody RandomProductUrlVO randomProductUrlVO){

        String url = seckillProductService.updateUrl(randomProductUrlVO);
        return new CommonResult<>(200, "成功", url);
    }

}

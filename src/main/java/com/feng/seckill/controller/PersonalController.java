package com.feng.seckill.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feng.seckill.entitys.po.BreakRulePO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.service.PersonalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : pcf
 * @date : 2022/2/13 21:32
 */
@Api(tags = "前台：个人信息设置")
@RequestMapping(value = "/personal")
@RestController
public class PersonalController {

    @Autowired
    private PersonalService personalService;

    @GetMapping(value = "/get/break/rules")
    @ApiOperation(value = "获得个人破坏规则的记录", httpMethod = "GET")
    public CommonResult<List<BreakRulePO>> getPersonalBreakRules(HttpServletRequest request){

        List<BreakRulePO> list = personalService.getPersonalBreakRules(request);
        return new CommonResult<>(200, "成功", list);
    }

    @GetMapping(value = "/get/product")
    @ApiOperation(value = "获得个人成功购买的产品记录", httpMethod = "GET")
    public CommonResult<IPage<SeckillResultPO>> getPersonalProduct(
            @ApiParam(value = "当前页码", name = "current", example = "1", defaultValue = "1")
                @RequestParam(value = "current", required = false, defaultValue = "1") Long current,
            @ApiParam(value = "每页最大数据量", name = "maxLimit", example = "1", defaultValue = "10")
                @RequestParam(value = "maxLimit", required = false, defaultValue = "10") Long maxLimit,
                HttpServletRequest request){

        IPage<SeckillResultPO> page = personalService.getPersonalProduct(request, new HelpPage(current, maxLimit));
        return new CommonResult<>(200, "成功", page);
    }

}

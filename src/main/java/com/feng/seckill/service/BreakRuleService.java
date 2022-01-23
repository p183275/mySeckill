package com.feng.seckill.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.BreakRulePO;
import com.feng.seckill.entitys.vo.BreakRuleVO;
import com.feng.seckill.entitys.vo.HelpPage;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 20:01
 */
public interface BreakRuleService extends IService<BreakRulePO> {

    /**
     * 检索分页查询
     * @param key 关键字
     * @param status 状态
     * @param helpPage 分页属性
     * @return 分页数据
     */
    Page<BreakRulePO> queryPage(String key, String status, HelpPage helpPage);

    /**
     * 增加破坏规则的记录
     * @param breakRuleVO 破坏规则封装类
     */
    void addBreakRuleRecord(BreakRuleVO breakRuleVO);

    /**
     * 修改破坏规则的记录
     * @param breakRuleVO 破坏规则
     */
    void updateBreakRuleRecord(BreakRuleVO breakRuleVO);

    /**
     * 删除破坏规则的记录
     * @param breakIdList 主键
     */
    void deleteBreakRuleRecord(List<Long> breakIdList);

}

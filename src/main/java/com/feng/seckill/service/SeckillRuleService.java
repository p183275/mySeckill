package com.feng.seckill.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.SeckillRulePO;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.SeckillRuleVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author : pcf
 * @date : 2022/1/17 15:04
 */
public interface SeckillRuleService extends IService<SeckillRulePO> {

    /**
     * 分页查询规则数据
     * @param helpPage 分页封装
     * @return 分页数据
     */
    Page<SeckillRulePO> queryPage(HelpPage helpPage);

    /**
     * 增加规则
     * @param seckillRuleVO 规则封装类
     */
    void addSeckillRule(SeckillRuleVO seckillRuleVO);

    /**
     * 修应规则
     * @param seckillRuleVO 规则封装类
     */
    void updateSeckillRule(SeckillRuleVO seckillRuleVO);

    /**
     * 删除规则
     * @param ruleIdList 规则id集合
     */
    void deleteSeckillRule(List<Long> ruleIdList);

    /**
     * 指定初筛规则
     * @param ruleIdList 规则id
     */
    void addRulesToActivity(List<Long> ruleIdList);

    /**
     * 通过用户id查询是否有资格参加秒杀活动
     * @param userPO 用户信息封装类
     * @return false or true
     */
    boolean getPermissionByUserId(UserPO userPO) throws ExecutionException, InterruptedException;

    /**
     * 并发请求拿到秒杀活动的规则
     * @return 规则集合
     */
    List<SeckillRulePO> showRules();

    /**
     * 拿到配置参数
     * @param ruleId 规则id
     * @return 配置参数
     */
    Map<String, String> getConfigVariable(Long ruleId);

    /**
     * 修改配置参数
     * @param ruleId 规则id
     * @param mapParam 配置参数
     */
    void updateConfigVariable(Long ruleId, Map<String, String> mapParam);
}

package com.feng.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.constant.SeckillRuleConstant;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.po.SeckillRulePO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.SeckillRuleVO;
import com.feng.seckill.mapper.BreakRuleMapper;
import com.feng.seckill.mapper.SeckillRuleMapper;
import com.feng.seckill.service.BreakRuleService;
import com.feng.seckill.service.SeckillRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : pcf
 * @date : 2022/1/17 15:04
 */
@Slf4j
@Service
public class SeckillRuleServiceImpl extends ServiceImpl<SeckillRuleMapper, SeckillRulePO>
        implements SeckillRuleService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SeckillRuleMapper seckillRuleMapper;
    @Autowired
    private BreakRuleMapper breakRuleMapper;

    /**
     * 分页查询规则数据
     * @param helpPage 分页封装
     * @return 分页数据
     */
    @Override
    public Page<SeckillRulePO> queryPage(HelpPage helpPage) {

        QueryWrapper<SeckillRulePO> queryWrapper = new QueryWrapper<>();

        Page<SeckillRulePO> seckillRulePOPage = new Page<>();

        // 复制属性
        BeanUtils.copyProperties(helpPage, seckillRulePOPage);

        // 返回对象
        return seckillRuleMapper.selectPage(seckillRulePOPage, queryWrapper);

    }

    /**
     * 增加规则
     * @param seckillRuleVO 规则封装类
     */
    @Override
    public void addSeckillRule(SeckillRuleVO seckillRuleVO) {

        // 判断是否为空
        if (seckillRuleVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象复制属性
        SeckillRulePO seckillRulePO = new SeckillRulePO();
        BeanUtils.copyProperties(seckillRuleVO, seckillRulePO);

        // 设置生效状态为未生效
        seckillRulePO.setRuleStatus(SeckillRuleConstant.RuleStatus.NOT_EFFECT.getCode());
        // 设置创建时间
        seckillRulePO.setCreateDate(new Date());

        // 存入数据库
        seckillRuleMapper.insert(seckillRulePO);
    }

    /**
     * 修改规则
     * @param seckillRuleVO 规则封装类
     */
    @Override
    public void updateSeckillRule(SeckillRuleVO seckillRuleVO) {

        // 判断是否为空
        if (seckillRuleVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象复制属性
        SeckillRulePO seckillRulePO = new SeckillRulePO();
        BeanUtils.copyProperties(seckillRuleVO, seckillRulePO);

        // 更新数据
        seckillRuleMapper.updateById(seckillRulePO);
    }

    /**
     * 删除规则
     * @param ruleIdList 规则id集合
     */
    @Override
    public void deleteSeckillRule(List<Long> ruleIdList) {

        // 判断数据是否为空
        if (ruleIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        seckillRuleMapper.deleteBatchIds(ruleIdList);
    }

    /**
     * 制定初筛规则
     * @param ruleIdList 规则id
     */
    @Override
    public void addRulesToActivity(List<Long> ruleIdList) {
        // 判断是否为空
        if (ruleIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 封装规则类
        List<SeckillRulePO> seckillRulePOS = ruleIdList.stream().map(ruleId -> {
            // 创建对象
            SeckillRulePO seckillRulePO = new SeckillRulePO();
            seckillRulePO.setRuleId(ruleId);
            // 设置状态为正在生效
            seckillRulePO.setRuleStatus(SeckillRuleConstant.RuleStatus.EFFECT.getCode());
            return seckillRulePO;
        }).collect(Collectors.toList());

        // 调用更新方法批量修改
        this.updateBatchById(seckillRulePOS);
    }

    /**
     * 通过用户id查询是否有资格参加秒杀活动
     * @param userId 用户id
     * @return false true
     */
    @Override
    public boolean getPermissionByUserId(Long userId) {

        // 从redis中获取对象
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String s = operations.get(RedisConstant.BREAK_RULE_IDS);

        // 如果s为空
        if (!StringUtils.hasLength(s)){

            // 对象加锁
            synchronized (this){
                // 再次查询redis
                String s1 = operations.get(RedisConstant.BREAK_RULE_IDS);

                // 仍然为空
                if (s1 == null){
                    // 查库
                    QueryWrapper<SeckillRulePO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("rule_status", SeckillRuleConstant.RuleStatus.EFFECT.getCode());
                    List<SeckillRulePO> seckillRulePOS = seckillRuleMapper.selectList(queryWrapper);

                    // 以拿出idd
                    Set<Long> ruleIdSet = seckillRulePOS.stream().map(SeckillRulePO::getRuleId)
                            .collect(Collectors.toSet());

                    String jsonString = JSON.toJSONString(ruleIdSet);

                    // 存入redis，设置过期时间为10分钟
                    operations.set(RedisConstant.BREAK_RULE_IDS, jsonString,
                            RedisConstant.BREAK_RULE_IDS_EXPIRED_TIME, TimeUnit.MINUTES);
                    return judge(ruleIdSet, userId);
                }

                Set<Long> ruleIdSet = JSON.parseObject(s1, new TypeReference<Set<Long>>() {});
                return judge(ruleIdSet, userId);
            }// 释放锁
        }

        // 如果s不为空
        Set<Long> ruleSet = JSON.parseObject(s, new TypeReference<Set<Long>>() {});

        return judge(ruleSet, userId);
    }

    /**
     * 并发请求拿到秒杀活动的规则
     * @return 规则集合
     */
    @Override
    public List<SeckillRulePO> showRules() {

        // 从redis中拿到规则
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String rulesFromRedis = operations.get(RedisConstant.EFFECT_RULES);

        if (rulesFromRedis == null){
            // 加锁
            synchronized (this){
                // 再次判断redis中有无
                String rulesFromRedis2 = operations.get(RedisConstant.EFFECT_RULES);
                if (rulesFromRedis2 == null){
                    // 查库
                    QueryWrapper<SeckillRulePO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("rule_status", SeckillRuleConstant.RuleStatus.EFFECT.getCode());
                    List<SeckillRulePO> seckillRulePOS = seckillRuleMapper.selectList(queryWrapper);

                    // 传为json字符串并存入redis
                    String jsonString = JSON.toJSONString(seckillRulePOS);
                    // 设置规则过期时间为15分钟
                    operations.set(RedisConstant.EFFECT_RULES, jsonString,
                            RedisConstant.EFFECT_RULES_EXPIRED_TIME, TimeUnit.MINUTES);

                    return seckillRulePOS;
                }
                // 如果有
                return JSON.parseObject(rulesFromRedis2, new TypeReference<List<SeckillRulePO>>(){});
            }
        }

        return JSON.parseObject(rulesFromRedis, new TypeReference<List<SeckillRulePO>>(){});
    }

    /******************************************************************************************************
     *************************************    方法    ******************************************************
     ******************************************************************************************************/
    public boolean judge(Set<Long> ruleSet, Long userId){

        // 通过userId查询破坏的规则
        List<Long> userIdList = breakRuleMapper.getRuleIdListByUserId(userId);

        // 如果为空则直接有资格
        if (userIdList.isEmpty()) return true;

        // 遍历进行判断
        for (Long item : userIdList){
            // 如果由则不合格
            if (ruleSet.contains(item)) return false;
        }

        return false;
    }
}

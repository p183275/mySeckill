package com.feng.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.config.OverdueConfig;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.constant.SeckillRuleConstant;
import com.feng.seckill.entitys.constant.UserInfoConstant;
import com.feng.seckill.entitys.po.OverdueRecordPO;
import com.feng.seckill.entitys.po.SeckillRulePO;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.SeckillRuleVO;
import com.feng.seckill.mapper.BreakRuleMapper;
import com.feng.seckill.mapper.SeckillRuleMapper;
import com.feng.seckill.service.SeckillRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
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
    @Autowired
    private RiskyDecisionEngineServiceImpl riskyDecisionEngineService;

    /**
     * ????????????????????????
     * @param helpPage ????????????
     * @return ????????????
     */
    @Override
    public Page<SeckillRulePO> queryPage(HelpPage helpPage) {

        QueryWrapper<SeckillRulePO> queryWrapper = new QueryWrapper<>();

        Page<SeckillRulePO> seckillRulePOPage = new Page<>();

        // ????????????
        BeanUtils.copyProperties(helpPage, seckillRulePOPage);

        // ????????????
        return seckillRuleMapper.selectPage(seckillRulePOPage, queryWrapper);

    }

    /**
     * ????????????
     * @param seckillRuleVO ???????????????
     */
    @Override
    public void addSeckillRule(SeckillRuleVO seckillRuleVO) {

        // ??????????????????
        if (seckillRuleVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // ????????????????????????
        SeckillRulePO seckillRulePO = new SeckillRulePO();
        BeanUtils.copyProperties(seckillRuleVO, seckillRulePO);

        // ??????????????????????????????
        seckillRulePO.setRuleStatus(SeckillRuleConstant.RuleStatus.NOT_EFFECT.getCode());
        // ??????????????????
        seckillRulePO.setCreateDate(new Date());

        // ???????????????
        seckillRuleMapper.insert(seckillRulePO);
    }

    /**
     * ????????????
     * @param seckillRuleVO ???????????????
     */
    @Override
    public void updateSeckillRule(SeckillRuleVO seckillRuleVO) {

        // ??????????????????
        if (seckillRuleVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // ????????????????????????
        SeckillRulePO seckillRulePO = new SeckillRulePO();
        BeanUtils.copyProperties(seckillRuleVO, seckillRulePO);

        // ???????????????????????????
        redisTemplate.delete(RedisConstant.EFFECT_RULES);

        // ????????????
        seckillRuleMapper.updateById(seckillRulePO);
    }

    /**
     * ????????????
     * @param ruleIdList ??????id??????
     */
    @Override
    public void deleteSeckillRule(List<Long> ruleIdList) {

        // ????????????????????????
        if (ruleIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // ????????????????????????????????????
        redisTemplate.delete(RedisConstant.EFFECT_RULES);

        seckillRuleMapper.deleteBatchIds(ruleIdList);
    }

    /**
     * ??????????????????
     * @param ruleIdList ??????id
     */
    @Override
    public void addRulesToActivity(List<Long> ruleIdList) {
        // ??????????????????
        if (ruleIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // ???????????????
        List<SeckillRulePO> seckillRulePOS = ruleIdList.stream().map(ruleId -> {
            // ????????????
            SeckillRulePO seckillRulePO = new SeckillRulePO();
            seckillRulePO.setRuleId(ruleId);
            // ???????????????????????????
            seckillRulePO.setRuleStatus(SeckillRuleConstant.RuleStatus.EFFECT.getCode());
            return seckillRulePO;
        }).collect(Collectors.toList());

        // ???????????????????????????
        redisTemplate.delete(RedisConstant.EFFECT_RULES);

        // ??????????????????????????????
        this.updateBatchById(seckillRulePOS);
    }

    /**
     * ????????????id???????????????????????????????????????
     * @param userPO ??????????????????
     * @return false true
     */
    @Override
    public boolean getPermissionByUserId(UserPO userPO) throws ExecutionException, InterruptedException {

        // ???redis???????????????
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String s = operations.get(RedisConstant.BREAK_RULE_IDS);

        // ??????s??????
        if (!StringUtils.hasLength(s)){

            // ????????????
            synchronized (this){
                // ????????????redis
                String s1 = operations.get(RedisConstant.BREAK_RULE_IDS);

                // ????????????
                if (s1 == null){
                    // ??????
                    QueryWrapper<SeckillRulePO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("rule_status", SeckillRuleConstant.RuleStatus.EFFECT.getCode());
                    List<SeckillRulePO> seckillRulePOS = seckillRuleMapper.selectList(queryWrapper);

                    // ?????????id
                    Set<Long> ruleIdSet = seckillRulePOS.stream().map(SeckillRulePO::getRuleId)
                            .collect(Collectors.toSet());

                    String jsonString = JSON.toJSONString(ruleIdSet);

                    // ??????redis????????????????????????10??????
                    operations.set(RedisConstant.BREAK_RULE_IDS, jsonString,
                            RedisConstant.BREAK_RULE_IDS_EXPIRED_TIME, TimeUnit.MINUTES);
                    return judge(ruleIdSet, userPO);
                }

                Set<Long> ruleIdSet = JSON.parseObject(s1, new TypeReference<Set<Long>>() {});
                return judge(ruleIdSet, userPO);
            }// ?????????
        }

        // ??????s?????????
        Set<Long> ruleSet = JSON.parseObject(s, new TypeReference<Set<Long>>() {});

        return judge(ruleSet, userPO);
    }

    /**
     * ???????????????????????????????????????
     * @return ????????????
     */
    @Override
    public List<SeckillRulePO> showRules() {

        // ???redis???????????????
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String rulesFromRedis = operations.get(RedisConstant.EFFECT_RULES);

        if (rulesFromRedis == null){
            // ??????
            synchronized (this){
                // ????????????redis?????????
                String rulesFromRedis2 = operations.get(RedisConstant.EFFECT_RULES);
                if (rulesFromRedis2 == null){
                    // ??????
                    QueryWrapper<SeckillRulePO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("rule_status", SeckillRuleConstant.RuleStatus.EFFECT.getCode());
                    List<SeckillRulePO> seckillRulePOS = seckillRuleMapper.selectList(queryWrapper);

                    // ????????? ???????????? "-"
                    List<SeckillRulePO> ruleCollect = seckillRulePOS.stream().peek(item -> {
                        // ????????????
                        String ruleContent = item.getRuleContent();
                        String newRuleContent = ruleContent.replace("-", "");
                        item.setRuleContent(newRuleContent);
                    }).collect(Collectors.toList());

                    // ??????json??????????????????redis
                    String jsonString = JSON.toJSONString(ruleCollect);
                    // ???????????????????????????15??????
                    operations.set(RedisConstant.EFFECT_RULES, jsonString,
                            RedisConstant.EFFECT_RULES_EXPIRED_TIME, TimeUnit.MINUTES);

                    return ruleCollect;
                }
                // ?????????
                return JSON.parseObject(rulesFromRedis2, new TypeReference<List<SeckillRulePO>>(){});
            }
        }

        return JSON.parseObject(rulesFromRedis, new TypeReference<List<SeckillRulePO>>(){});
    }

    /**
     * ??????????????????
     * @param ruleId ??????id
     * @return ????????????
     */
    @Override
    public Map<String, String> getConfigVariable(Long ruleId) {

        // ?????? map
        Map<String, String> configMap = new HashMap<>();

        // redis ????????????
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // ????????????
        if (ruleId == 1){
            // ??????????????????
            String s = operations.get(RedisConstant.FILTER_OVERDUE_ENTITY);
            // ????????????
            if (s == null){
                OverdueConfig overdueConfig = new OverdueConfig();
                 configMap = transformOverdueConfigToMap(overdueConfig);
            }else {// ?????????
                // json ??????
                OverdueConfig overdueConfig = JSON.parseObject(s, OverdueConfig.class);
                configMap = transformOverdueConfigToMap(overdueConfig);
            }
        }else if (ruleId == 3){ // ????????????
            // ??? redis ?????????????????????
            String workStatus = redisTemplate.opsForValue().get(RedisConstant.FILTER_WORK_STATUS);
            workStatus = workStatus == null ? UserInfoConstant.WorkStatus.NO_WORK.getCode() : workStatus;
            configMap.put("workStatus", workStatus);
        }else if (ruleId == 5){ // ????????????
            // ??? redis ?????????????????????
            String age = redisTemplate.opsForValue().get(RedisConstant.FILTER_AGE);
            // ??????????????????
            age = age == null ? "18" : age;
            configMap.put("age", age);
        }else {
            configMap.put("message", "???????????????");
        }

        return configMap;
    }

    /**
     * ??????????????????
     * @param ruleId ??????id
     * @param mapParam ????????????
     */
    @Override
    public void updateConfigVariable(Long ruleId, Map<String, String> mapParam) {
        // ?????? redis ????????????
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        // ??????????????????
        if (ruleId == 1){
            // ????????????
            OverdueConfig overdueConfig = transformMapToOverdueConfig(mapParam);
            // ?????? json
            String jsonString = JSON.toJSONString(overdueConfig);
            operations.set(RedisConstant.FILTER_OVERDUE_ENTITY, jsonString);
        }else if (ruleId == 2){ // ??????????????????
            // ????????????
            String workStatus = mapParam.get("workStatus");
            // ?????? redis
            operations.set(RedisConstant.FILTER_WORK_STATUS, workStatus);
        }else if (ruleId == 4){ // ????????????
            String age = mapParam.get("age");
            // ??????redis
            operations.set(RedisConstant.FILTER_AGE, age);
        }
    }


    /******************************************************************************************************
     *************************************    ??????    ******************************************************
     ******************************************************************************************************/
    public boolean judge(Set<Long> ruleSet, UserPO userPO) throws ExecutionException, InterruptedException {

        // TODO ??????????????????
        // ????????????????????????true
        return riskyDecisionEngineService.judgeByAsc(ruleSet, userPO);
    }

    public Map<String, String> transformOverdueConfigToMap(OverdueConfig config){
        Map<String, String> map = new HashMap<>();
        map.put("overdueYear", config.getOverdueYear().toString());
        map.put("overdueTimes", config.getOverdueTimes().toString());
        map.put("overdueMinMoney", config.getOverdueMinMoney().toString());
        map.put("overdueDay", config.getOverdueDay().toString());
        return map;
    }

    public OverdueConfig transformMapToOverdueConfig(Map<String, String> map){

        // ????????????
        String overdueYear = map.get("overdueYear");
        String overdueTimes = map.get("overdueTimes");
        String overdueMinMoney = map.get("overdueMinMoney");
        String overdueDay = map.get("overdueDay");

        // ?????? OverdueConfig
        return new OverdueConfig(Integer.parseInt(overdueYear),
                Integer.parseInt(overdueTimes),
                new BigDecimal(overdueMinMoney),
                Integer.parseInt(overdueDay));
    }
}

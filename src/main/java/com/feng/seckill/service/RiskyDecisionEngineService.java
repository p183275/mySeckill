package com.feng.seckill.service;

import com.feng.seckill.entitys.po.UserPO;

import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author : pcf
 * @date : 2022/2/14 20:49
 * 风险决策引擎
 */
public interface RiskyDecisionEngineService {

    /**
     * 逾期过滤
     * @param userId 用户id
     * @return true or false
     */
    boolean overdueFilter(Long userId);

    /**
     * 工作状态过滤
     * @param userPO 用户信息封装
     * @return true or false
     */
    boolean workStatusFilter(UserPO userPO);

    /**
     * 失信人过滤
     * @param userId 用户id
     * @return true or false
     */
    boolean defaulterFilter(Long userId);

    /**
     * 年龄过滤
     * @param userPO 用户信息封装类
     * @return true or false
     */
    boolean ageFilter(UserPO userPO);

    /**
     * 异步判断用户是否有资格参加活动
     * @param BreakRuleIds 开启的规则
     * @param userPO 用户信息
     * @return true or false
     * @throws ExecutionException 异常
     * @throws InterruptedException 异常
     */
    boolean judgeByAsc(Set<Long> BreakRuleIds, UserPO userPO) throws ExecutionException, InterruptedException;

}

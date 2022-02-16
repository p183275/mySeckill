package com.feng.seckill.service;

import com.feng.seckill.entitys.po.UserPO;

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

}

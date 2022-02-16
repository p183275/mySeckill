package com.feng.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feng.seckill.entitys.config.OverdueConfig;
import com.feng.seckill.entitys.constant.BreakRuleConstant;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.constant.UserInfoConstant;
import com.feng.seckill.entitys.po.CreditPO;
import com.feng.seckill.entitys.po.OverdueRecordPO;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.mapper.CreditMapper;
import com.feng.seckill.mapper.OverdueRecordMapper;
import com.feng.seckill.service.RiskyDecisionEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : pcf
 * @date : 2022/2/14 20:49
 */
@Service
public class RiskyDecisionEngineServiceImpl implements RiskyDecisionEngineService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OverdueRecordMapper overdueRecordMapper;
    @Autowired
    private CreditMapper creditMapper;

    /**
     * 逾期过滤
     * @param userId 用户id
     * @return true
     */
    @Override
    public boolean overdueFilter(Long userId) {

        // 从 redis 中拿到逾期配置类
        String s = redisTemplate.opsForValue().get(RedisConstant.FILTER_OVERDUE_ENTITY);

        // 转为逾期配置类
        OverdueConfig overdueConfig = JSON.parseObject(s, OverdueConfig.class);
        // 判断是否为空
        if (overdueConfig == null){
            // 如果为空 则调用无参构造创建默认值
            overdueConfig = new OverdueConfig();
            // 转为 json 字符串
            String jsonString = JSON.toJSONString(overdueConfig);
            // 放入 redis
            redisTemplate.opsForValue().set(RedisConstant.FILTER_OVERDUE_ENTITY, jsonString);
        }

        // 拿到用户逾期记录
        QueryWrapper<OverdueRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<OverdueRecordPO> overdueRecordPOS = overdueRecordMapper.selectList(queryWrapper);

        // 如果逾期记录为 0 直接通过
        if (overdueRecordPOS.isEmpty()) return true;

        // 拿到当前时间
        Calendar instance = Calendar.getInstance();
        Date currentTime = instance.getTime();

        // 拿出配置条件
        OverdueConfig finalOverdueConfig = overdueConfig;
        // 最小逾期次数
        Integer overdueTimes = finalOverdueConfig.getOverdueTimes();
        // 最小逾期金额
        BigDecimal overdueMinMoney = finalOverdueConfig.getOverdueMinMoney();
        // 最小逾期年限
        Integer overdueYear = finalOverdueConfig.getOverdueYear();
        // 最小逾期天数
        Integer overdueDay = finalOverdueConfig.getOverdueDay();


        // 过滤流式编程
        List<OverdueRecordPO> collect = overdueRecordPOS.stream().filter(item -> {
            // 判断逾期金额
            BigDecimal overMoney = item.getOverMoney();
            boolean moneyJudge = overMoney.compareTo(overdueMinMoney) < 0;

            // 判断是否过了三年
            Date beginTime = item.getBeginTime();
            // 设置日历为 beginTime
            instance.setTime(beginTime);
            instance.add(Calendar.YEAR, overdueYear);
            Date afterAddYearTime = instance.getTime();
            boolean yearJudge = afterAddYearTime.after(currentTime);

            // 判断是否在三天之内
            // 继续设置为beginTime
            instance.setTime(beginTime);
            instance.add(Calendar.DAY_OF_WEEK, overdueDay);
            Date afterAddDayTime = instance.getTime();
            Date endTime = item.getEndTime();
            boolean dayJudge = afterAddDayTime.after(endTime);

            // 全部满足则通过
            return moneyJudge && yearJudge && dayJudge;
        }).collect(Collectors.toList());

        // 判断逾期次数 是否小于 最小逾期次数
        return collect.size() <= overdueTimes;
    }

    /**
     * 工作状态过滤
     * @param userPO 用户信息封装
     * @return true or false
     */
    @Override
    public boolean workStatusFilter(UserPO userPO) {

        // 从 redis 中拿到工作状态
        String workStatus = redisTemplate.opsForValue().get(RedisConstant.FILTER_WORK_STATUS);

        workStatus = workStatus == null ? UserInfoConstant.WorkStatus.NO_WORK.getCode() : workStatus;

        return !userPO.getWorkStatus().equals(workStatus);
    }

    /**
     * 失信人过滤
     * @param userId 用户id
     * @return true or false
     */
    @Override
    public boolean defaulterFilter(Long userId) {

        QueryWrapper<CreditPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("status", BreakRuleConstant.CreditStatus.EFFECT.getCode());

        Integer count = creditMapper.selectCount(queryWrapper);

        return count == 0;
    }

    /**
     * 年龄过滤
     * @param userPO 用户信息封装类
     * @return true or false
     */
    @Override
    public boolean ageFilter(UserPO userPO) {

        // 从 redis 中拿到年龄限制
        String age = redisTemplate.opsForValue().get(RedisConstant.FILTER_AGE);

        age = age == null ? "18" : age;

        return userPO.getAge() >= Integer.parseInt(age);
    }

}

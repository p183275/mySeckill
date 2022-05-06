package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.BreakRuleConstant;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.po.*;
import com.feng.seckill.entitys.vo.BreakRuleDescVO;
import com.feng.seckill.entitys.vo.BreakRuleVO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.entitys.vo.SeckillRuleVO;
import com.feng.seckill.mapper.*;
import com.feng.seckill.service.BreakRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 20:02
 */
@Service
public class BreakRuleServiceImpl extends ServiceImpl<BreakRuleMapper, BreakRulePO>
        implements BreakRuleService {

    @Autowired
    private BreakRuleMapper breakRuleMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private SeckillRuleMapper seckillRuleMapper;
    @Autowired
    private CreditMapper creditMapper;
    @Autowired
    private OverdueRecordMapper overdueRecordMapper;

    /**
     * 检索分页查询
     * @param key 关键字
     * @param status 状态
     * @param helpPage 分页属性
     * @return 分页数据
     */
    @Override
    public Page<BreakRulePO> queryPage(String key, String status, HelpPage helpPage) {

        // 创建分页数据
        Page<BreakRulePO> breakRulePOPage = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(helpPage, breakRulePOPage);

        // 新建包装器
        QueryWrapper<BreakRulePO> queryWrapper = new QueryWrapper<>();

        if (status != null && status.length() > 0){
            queryWrapper.eq("status", status);
        }
        if (key != null && key.length() > 0){
            queryWrapper.and(wrapper -> wrapper.eq("user_id", key)
                    .or().like("user_name", key)
                    .or().like("rule_name", key));
        }

        return this.baseMapper.selectPage(breakRulePOPage, queryWrapper);
    }

    /**
     * 增加破坏规则的记录
     * @param breakRuleVO 破坏规则封装类
     */
    @Override
    public void addBreakRuleRecord(BreakRuleVO breakRuleVO) {

        // 判断数据是否为空
        if (breakRuleVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象复制属性
        BreakRulePO breakRulePO = new BreakRulePO();
        BeanUtils.copyProperties(breakRuleVO, breakRulePO);

        // 查库拿到用户对象
        String userName = null;
        if (breakRulePO.getUserId() != null){
            UserPO userPO = userInfoMapper.selectById(breakRulePO.getUserId());
            userName = userPO.getName();
        }

        // 查库拿到规则名称
        String ruleName = null;
        if (breakRulePO.getRuleId() != null){
            SeckillRulePO seckillRulePO = seckillRuleMapper.selectById(breakRulePO.getRuleId());
            ruleName = seckillRulePO.getRuleName();
        }

        // 设置属性
        breakRulePO.setStatus(BreakRuleConstant.BreakStatus.EFFECT.getCode());
        breakRulePO.setUserName(userName);
        breakRulePO.setRuleName(ruleName);
        breakRulePO.setBeginTime(new Date());

        // 存入数据库
        breakRuleMapper.insert(breakRulePO);
    }

    /**
     * 修改破坏规则的记录
     * @param breakRuleVO 破坏规则
     */
    @Override
    public void updateBreakRuleRecord(BreakRuleVO breakRuleVO) {

        // 判断数据是否为空
        if (breakRuleVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象复制属性
        BreakRulePO breakRulePO = new BreakRulePO();
        BeanUtils.copyProperties(breakRuleVO, breakRulePO);

        // 更新
        breakRuleMapper.updateById(breakRulePO);
    }

    /**
     * 删除破坏规则的记录
     * @param breakIdList 主键
     */
    @Override
    public void deleteBreakRuleRecord(List<Long> breakIdList) {

        // 判空
        if (breakIdList.isEmpty()){
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);
        }

        // 删除
        breakRuleMapper.deleteBatchIds(breakIdList);
    }

    @Override
    public BreakRuleDescVO getDes(Long ruleId, Long recordId) {

        // 创建对象
        BreakRuleDescVO breakRuleDesc = new BreakRuleDescVO();

        // 查询 规则 信息
        SeckillRulePO seckillRulePO = seckillRuleMapper.selectById(ruleId);
        SeckillRuleVO seckillRuleVO = new SeckillRuleVO();
        // 复制属性
        BeanUtils.copyProperties(seckillRulePO, seckillRuleVO);
        breakRuleDesc.setRuleVO(seckillRuleVO);

        // 如果是 逾期记录
        if (ruleId.equals(BreakRuleConstant.RuleType.OVERDUE.getRuleType())){
            OverdueRecordPO overdueRecordPO = overdueRecordMapper.selectById(recordId);
            breakRuleDesc.setInfo(overdueRecordPO);

        }else if (ruleId.equals(BreakRuleConstant.RuleType.CREDIT.getRuleType())){ // 如果是失信记录
            CreditPO creditPO = creditMapper.selectById(recordId);
            breakRuleDesc.setInfo(creditPO);
        }
        return breakRuleDesc;
    }
}

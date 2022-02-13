package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.seckill.entitys.po.BreakRulePO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.mapper.BreakRuleMapper;
import com.feng.seckill.mapper.SeckillResultMapper;
import com.feng.seckill.service.PersonalService;
import com.feng.seckill.util.JWTUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : pcf
 * @date : 2022/2/13 21:37
 */
@Service
public class PersonalServiceImpl implements PersonalService {

    @Autowired
    private BreakRuleMapper breakRuleMapper;
    @Autowired
    private SeckillResultMapper seckillResultMapper;

    /**
     * 获得个人破坏规则的记录
     * @param request 请求
     * @return 破坏规则的记录
     */
    @Override
    public List<BreakRulePO> getPersonalBreakRules(HttpServletRequest request) {
        // 拿到 token
        String token = request.getHeader("token");
        // 从 token 中拿到用户id
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        // 创建包装类
        QueryWrapper<BreakRulePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(userId));
        // 按时间顺序
        queryWrapper.orderByDesc("begin_time");

        return breakRuleMapper.selectList(queryWrapper);
    }

    /**
     * 获得个人成功秒杀的产品记录
     * @param request 请求
     * @param helpPage 分页数据
     * @return 记录
     */
    @Override
    public IPage<SeckillResultPO> getPersonalProduct(HttpServletRequest request, HelpPage helpPage){

        // 拿到 token
        String token = request.getHeader("token");
        // 从 token 中拿到用户id
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        // 创建包装类
        QueryWrapper<SeckillResultPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(userId));
        // 按时间顺序
        queryWrapper.orderByDesc("create_date");

        Page<SeckillResultPO> page = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(helpPage, page);

        return seckillResultMapper.selectPage(page, queryWrapper);
    }
}

package com.feng.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feng.seckill.entitys.po.BreakRulePO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.vo.HelpPage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : pcf
 * @date : 2022/2/13 21:37
 */
public interface PersonalService {

    /**
     * 清除黑名单
     */
    void reflashBlackNames();

    /**
     * 获得个人破坏规则的记录
     * @param request 请求
     * @return 破坏规则的记录
     */
    List<BreakRulePO> getPersonalBreakRules(HttpServletRequest request);

    /**
     * 获得个人成功秒杀的产品记录
     * @param request 请求
     * @param helpPage 分页数据
     * @return 记录
     */
    IPage<SeckillResultPO> getPersonalProduct(HttpServletRequest request, HelpPage helpPage);

    void createUser(Integer number);
}

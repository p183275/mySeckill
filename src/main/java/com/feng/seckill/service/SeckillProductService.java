package com.feng.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/1/16 15:39
 */
public interface SeckillProductService extends IService<SeckillProductPO> {

    /**
     * 分页查询产品数据
     * @param page 分页属性
     * @return 分页数据
     */
    IPage<SeckillProductVO> queryPage(HelpPage page);

    /**
     * 添加产品
     * @param addSeckillProductVO 产品属性
     */
    void addSeckillProduct(AddSeckillProductVO addSeckillProductVO);

    /**
     * 修改产品
     * @param addSeckillProductVO 产品属性
     */
    void updateSeckillProduct(SeckillProductVO addSeckillProductVO);

    /**
     * 删除产品
     * @param productIdList id集合
     */
    void deleteSeckillProduct(List<Long> productIdList);

    /**
     * 修改活动链接
     */
    String updateUrl(RandomProductUrlVO randomProductUrlVO);

    /**
     * 刷新产品数量
     */
    void reflashProductionNumber();

    /**
     * 并发请求展示商品
     * @return 商品集合
     */
    List<SeckillProductPO> showProductions();

    /**
     * 从redis中获取产品数量
     * @return 产品数量
     */
    Map<String, String> getProductNumberFromRedis();

    /**
     * 进行秒杀(事务方法)
     * @param dynamicUrl1 动态链接1
     * @param dynamicUrl12 动态链接1
     * @param mySeckillVO 秒杀信息封装
     * @param request 请求
     * @return 秒杀结果
     */
    String doSeckill(String dynamicUrl1, String dynamicUrl12, MySeckillVO mySeckillVO, HttpServletRequest request);

}

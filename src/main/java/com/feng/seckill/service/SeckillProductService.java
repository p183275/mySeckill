package com.feng.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
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


    List<SeckillProductPO> queryPage(String productStatus, String productPrice, String worth, String key);

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
     * 用户取消支付
     * @param request 请求
     * @param productId 商品id
     */
    void disPay(HttpServletRequest request, Long productId);

    /**
     * 用户付款
     * @param request 请求
     * @param payVO 支付封装类
     */
    void pay(HttpServletRequest request, PayVO payVO);

    /**
     * 拿到用户订单
     * @param request 用户id
     * @param productId 商品 id
     * @return 订单
     */
    SeckillResultPO getOrder(HttpServletRequest request, Long productId);

    /**
     * 判断用户有无未付款的订单
     * @param request 请求
     * @return 未付款的订单
     */
    SeckillResultPO checkOrdered(HttpServletRequest request);

    /**
     * 修改活动链接
     */
    String updateUrl(RandomProductUrlVO randomProductUrlVO);

    /**
     * 批量修改活动链接
     * @param voList 实体
     */
    void updateUrlBatch(List<RandomProductUrlVO> voList);

    /**
     * 刷新产品
     */
    void reflashProduction();

    /**
     * 周期刷新正在开始的活动的 url
     */
    void reflashProductUrl();

    /**
     * 并发请求展示商品
     * @return 商品集合
     */
    List<SeckillProductPO> showProductions();

    /**
     * 从redis中获取产品数量 (活动可以展示)
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

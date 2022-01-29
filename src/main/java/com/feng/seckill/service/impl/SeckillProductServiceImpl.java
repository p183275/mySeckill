package com.feng.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.constant.SeckillProductConstant;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.vo.*;
import com.feng.seckill.exception.entity.SQLDuplicateException;
import com.feng.seckill.mapper.SeckillProductMapper;
import com.feng.seckill.mapper.SeckillResultMapper;
import com.feng.seckill.service.SeckillProductService;
import com.feng.seckill.util.JWTUtils;
import com.feng.seckill.util.ProductUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : pcf
 * @date : 2022/1/16 15:40
 */
@Slf4j
@Service
public class SeckillProductServiceImpl extends ServiceImpl<SeckillProductMapper, SeckillProductPO>
        implements SeckillProductService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SeckillProductMapper seckillProductMapper;
    @Autowired
    private SeckillResultMapper seckillResultMapper;

    @Override
    public IPage<SeckillProductVO> queryPage(HelpPage page) {

        Page<SeckillProductVO> voPage = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(page, voPage);

        return seckillProductMapper.selectPageVo(voPage);
    }

    @Override
    public void addSeckillProduct(AddSeckillProductVO addSeckillProductVO) {

        // 判断数据是否为空
        if (addSeckillProductVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象并复制属性
        SeckillProductPO seckillProductPO = new SeckillProductPO();
        BeanUtils.copyProperties(addSeckillProductVO, seckillProductPO);

        // 添加商品时，将商品的数量以及连接放入redis
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        // 商品数量放入redis
        operations.set(RedisConstant.PRODUCTION_NUMBER + seckillProductPO.getProductId(),
                String.valueOf(seckillProductPO.getProductNumber()));


        // 设置属性
        // 状态为未开始
        seckillProductPO.setProductStatus(SeckillProductConstant.ProductStatus.NOT_START.getCode());
        // 设置创建时间
        seckillProductPO.setCreateDate(new Date());

       // 保存
        seckillProductMapper.insert(seckillProductPO);

    }

    /**
     * 修改产品
     * @param addSeckillProductVO 产品属性
     */
    @Override
    public void updateSeckillProduct(SeckillProductVO addSeckillProductVO) {

        // 判断数据是否为空
        if (addSeckillProductVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象并复制属性
        SeckillProductPO seckillProductPO = new SeckillProductPO();
        BeanUtils.copyProperties(addSeckillProductVO, seckillProductPO);

        // 判断有无修改商品数量
        Integer productNumber = seckillProductPO.getProductNumber();
        // 如果修改了商品的数量，则同时修改redis中的商品数量
        if (productNumber != null){
            redisTemplate.opsForValue().set(RedisConstant.PRODUCTION_NUMBER
                    + seckillProductPO.getProductId(), String.valueOf(productNumber));
        }
        // 删除产品信息
        redisTemplate.delete(RedisConstant.PRODUCTIONS);

        // 更新
        seckillProductMapper.updateById(seckillProductPO);
    }

    /**
     * 删除产品
     * @param productIdList id集合
     */
    @Override
    public void deleteSeckillProduct(List<Long> productIdList) {

        if (productIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 删除redis中的商品数量
        productIdList.forEach(item ->
                redisTemplate.delete(RedisConstant.PRODUCTION_NUMBER + item));


        // 不为空则删除
        seckillProductMapper.deleteBatchIds(productIdList);
    }

    /**
     * 修改链接
     */
    @Override
    public String updateUrl(RandomProductUrlVO randomProductUrlVO) {

        // 拿到数据
        Integer length = randomProductUrlVO.getLength();
        if (length < 20 || length > 200)
            throw new RuntimeException(ExceptionConstant.DATA_ILLEGAL_EXCEPTION);
        // 判断产品id是否为空
        Long productId = randomProductUrlVO.getProductId();
        if (productId == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 随机算法生成链接
        String url = ProductUtil.createRandomUrl(length);

        // 创建对象并设置属性
        SeckillProductPO seckillProductPO = new SeckillProductPO();
        seckillProductPO.setProductId(productId);
        seckillProductPO.setUrl(url);

        // 从redis中拿到连接
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        // 将连接写入redis
        String urlFromRedis = operations.get(RedisConstant.PRODUCTION_URL
                + randomProductUrlVO.getProductId());
        if (urlFromRedis != null){
            // 如果redis中有连接，则更新，若无则等待统一更新
            operations.set(RedisConstant.PRODUCTION_URL+randomProductUrlVO.getProductId(), url,
                    RedisConstant.PRODUCTION_URL_EXPIRED_TIME, TimeUnit.MINUTES);
        }

        // 更新链接
        seckillProductMapper.updateById(seckillProductPO);
        return url;
    }

    /**
     * 从redis中获取产品数量
     * @return 产品数量
     */
    @Override
    public Map<String, String> getProductNumberFromRedis() {

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        // 拿到产品列表
        List<SeckillProductPO> seckillProductPOS = showProductions();
        // 数据过滤
        return seckillProductPOS.stream().collect(Collectors.toMap(key -> key.getProductId().toString(), val -> {
            Long productId = val.getProductId();
            String numbers = operations.get(RedisConstant.PRODUCTION_NUMBER + productId);
            // 如果为空则返回"0" 否则返回number
            return numbers == null ? "0" : numbers;
        }));
    }

    /**
     * 并发请求展示商品
     * @return 商品集合
     */
    @Override
    public List<SeckillProductPO> showProductions() {

        // 先从redis中获取
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String stringOProductions = operations.get(RedisConstant.PRODUCTIONS);

        if (stringOProductions == null){
            // 加锁
            synchronized (this){
                // 再次查一遍redis
                String stringOProductions2 = operations.get(RedisConstant.PRODUCTIONS);

                // 判断
                if (stringOProductions2 == null){
                    // 查数据库
                    QueryWrapper<SeckillProductPO> queryWrapper = new QueryWrapper<>();
                    List<SeckillProductPO> seckillProductPOS = seckillProductMapper.selectList(queryWrapper);

                    // 转为json字符
                    String jsonString = JSON.toJSONString(seckillProductPOS);

                    // 将其存入redis，商品信息过期时间为5分钟
                    operations.set(RedisConstant.PRODUCTIONS, jsonString, RedisConstant.PRODUCTIONS_EXPIRED_TIME,
                            TimeUnit.MINUTES);
                    return seckillProductPOS;
                }
                // 如果不为空
                return JSON.parseObject(stringOProductions2,
                        new TypeReference<List<SeckillProductPO>>() {});
            }
        }
        // 如果不为空
        return JSON.parseObject(stringOProductions,
                new TypeReference<List<SeckillProductPO>>() {});
    }

    /**
     * 进行秒杀(事务方法)
     * @param dynamicUrl1 动态链接1
     * @param dynamicUrl12 动态链接1
     * @param mySeckillVO 秒杀信息封装
     * @param request 用户请求
     * @return 秒杀结果
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public String doSeckill(String dynamicUrl1, String dynamicUrl12, MySeckillVO mySeckillVO,
                            HttpServletRequest request) {

        // 判断动态链接是否为空
        if (dynamicUrl1 == null || dynamicUrl12 == null)
            throw new RuntimeException(SeckillProductConstant.URL_NULL);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // url判断
        boolean urlJudge = urlJudge(operations, mySeckillVO, dynamicUrl1, dynamicUrl12);

        if (!urlJudge)
            throw new RuntimeException(SeckillProductConstant.URL_FILED);

        // 进行产品秒杀
        boolean doingSeckill = doingSeckill(operations, mySeckillVO, request);

        // 抢购成功
        if (doingSeckill){
            return SeckillProductConstant.PRODUCTION_SUCCESS;
        }

        // 库存已空
        return SeckillProductConstant.PRODUCTION_FILED;
    }

    /****************************************************************************************************
     ***********************************************  方法  **********************************************
     *****************************************************************************************************/

    // 判断url
    public boolean urlJudge(ValueOperations<String, String> operations, MySeckillVO mySeckillVO,
                            String dynamicUrl1, String dynamicUrl2){

        // 拼接动态链接
        String dynamicUrl = SeckillProductConstant.PRODUCT_ACTIVITY_URL + dynamicUrl1 + "/" + dynamicUrl2;

        // 拿到产品id
        Long productId = mySeckillVO.getProductId();
        String productionUrl = operations.get(RedisConstant.PRODUCTION_URL + productId);

        // 如果链接为空
        if (productionUrl == null){
            // 上锁
            synchronized (this){
                String productionUrl2 = operations.get(RedisConstant.PRODUCTION_URL + productId);
                if (productionUrl2 == null){
                    // 将链接url放入redis
                    letUrlIntoRedis(operations);

                    String productionUrl3 = operations.get(RedisConstant.PRODUCTION_URL + productId);
                    // 判断
                    if (!dynamicUrl.equals(productionUrl3))
                        return false;

                    return true;
                }
                // 判断
                if (!dynamicUrl.equals(productionUrl2))
                    return false;

                return true;
            }
        }

        // 如果url不为空, 对比url
        if (!dynamicUrl.equals(productionUrl))
            return false;

        return true;
    }

    // 把url存入redis
    public void letUrlIntoRedis(ValueOperations<String, String> operations){
        QueryWrapper<SeckillProductPO> queryWrapper = new QueryWrapper<>();
        List<SeckillProductPO> seckillProductPOS = seckillProductMapper.selectList(queryWrapper);
        // 放入redis 过期时间为 20 分钟
        seckillProductPOS.forEach(item -> {
            operations.set(RedisConstant.PRODUCTION_URL + item.getProductId(), item.getUrl(),
                    RedisConstant.PRODUCTION_URL_EXPIRED_TIME, TimeUnit.MINUTES);
        });
    }

    // 进行秒杀
    private boolean doingSeckill(ValueOperations<String, String> operations, MySeckillVO mySeckillVO,
                                 HttpServletRequest request) {

        // 拿到产品id
        Long productId = mySeckillVO.getProductId();

        // 上锁
        synchronized (this){

            // 从redis中拿到产品数量
            String number = operations.get(RedisConstant.PRODUCTION_NUMBER + productId );

            number = number == null ? "0" : number;

            // 如果数量为0
            if (number.equals("0")){
                // 更新数据库
                seckillProductMapper.letProductionZero(productId);
                return false;
            }


            // 数据库中存入
            SeckillResultPO seckillResultPO = new SeckillResultPO();
            BeanUtils.copyProperties(mySeckillVO, seckillResultPO);
            seckillResultPO.setCreateDate(new Date());

            // 拿到当前用户id 和 姓名
            String token = request.getHeader("token");
            DecodedJWT verify = JWTUtils.verify(token);
            String userId = verify.getClaim("userId").asString();
            String userName = verify.getClaim("userName").asString();

            // 用户id和姓名放入对象
            seckillResultPO.setUserId(Long.parseLong(userId));
            seckillResultPO.setUserName(userName);

            // 存入数据
            try {
                seckillResultMapper.insert(seckillResultPO);
            }catch (DuplicateKeyException e){
                throw new SQLDuplicateException("不能重复购买");
            }

            // 如果数量不为0
            // redis中数量自减
            operations.decrement(RedisConstant.PRODUCTION_NUMBER + productId);
            return true;
        }
    }
}

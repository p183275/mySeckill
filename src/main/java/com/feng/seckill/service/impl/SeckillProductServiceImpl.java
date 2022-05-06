package com.feng.seckill.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.constant.OrderConstant;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.constant.SeckillProductConstant;
import com.feng.seckill.entitys.po.AccountPO;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.vo.*;
import com.feng.seckill.exception.entity.SQLDuplicateException;
import com.feng.seckill.mapper.AccountMapper;
import com.feng.seckill.mapper.SeckillProductMapper;
import com.feng.seckill.mapper.SeckillResultMapper;
import com.feng.seckill.mapper.UserInfoMapper;
import com.feng.seckill.service.SeckillProductService;
import com.feng.seckill.service.rabbitMq.DelayedQueueProducerService;
import com.feng.seckill.service.rabbitMq.WriteDBProducerService;
import com.feng.seckill.util.IPUtils;
import com.feng.seckill.util.JWTUtils;
import com.feng.seckill.util.ProductUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.lettuce.LettuceClusterConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private DelayedQueueProducerService delayedQueueProducerService;
    @Autowired
    private WriteDBProducerService writeDBProducerService;

    @Override
    public List<SeckillProductPO> queryPage(String productStatus, String productPrice, String worth, String key) {


        QueryWrapper<SeckillProductPO> queryWrapper = new QueryWrapper<>();

        if ((productStatus == null || productStatus.length() == 0) && (key == null || key.length() == 0)
        && (productPrice == null || productPrice.length() == 0) && (worth == null || worth.length() == 0)){
            return this.showProductions();
        }

        // 封装查询条件
        if (productStatus != null && productStatus.length() != 0){
            queryWrapper.eq("product_status", productStatus);
        }
        queryWrapper.eq("show_status", "0");

        // 关键字
        if (key != null && key.length() != 0){
            queryWrapper.like("product_name", key);
        }

        // 排序字段
        if (productPrice != null && productPrice.length() != 0){
            if ("ASC".equals(productPrice)){
                queryWrapper.orderByAsc("product_price");
            }else {
                queryWrapper.orderByDesc("product_price");
            }
        }

        if (worth != null && worth.length() != 0){
            if ("ASC".equals(worth)){
                queryWrapper.orderByAsc("worth");
            }else {
                queryWrapper.orderByDesc("worth");
            }
        }

        return seckillProductMapper.selectList(queryWrapper);
    }


    // 查库并刷新数据库
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public IPage<SeckillProductVO> queryPage(HelpPage page) {

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        Page<SeckillProductVO> voPage = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(page, voPage);

        IPage<SeckillProductVO> seckillProductVOIPage = seckillProductMapper.selectPageVo(voPage);
        List<SeckillProductVO> records = seckillProductVOIPage.getRecords();

        // 从 redis 中查到相关数量
        List<SeckillProductVO> collect = records.stream().peek(item -> {
            // 拿到产品 id
            Long productId = item.getProductId();
            String number = operations.get(RedisConstant.PRODUCTION_NUMBER + productId);
            number = number == null ? "0" : number;
            item.setProductNumber(Integer.parseInt(number));
        }).collect(Collectors.toList());

        // 将相关数量载入
        seckillProductVOIPage.setRecords(collect);

        // 修改数据库中的商品数量
        List<SeckillProductPO> seckillProductPOList = collect.stream().map(item -> {
            // 创建对象
            SeckillProductPO seckillProductPO = new SeckillProductPO();
            // 设置属性
            seckillProductPO.setProductId(item.getProductId());
            seckillProductPO.setProductNumber(item.getProductNumber());
            return seckillProductPO;
        }).collect(Collectors.toList());

        // 修改数量
        this.updateBatchById(seckillProductPOList);
        // 返回分页数据
        return seckillProductVOIPage;
    }

    @Override
    public void addSeckillProduct(AddSeckillProductVO addSeckillProductVO) {

        // 判断数据是否为空
        if (addSeckillProductVO == null)
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 创建对象并复制属性
        SeckillProductPO seckillProductPO = new SeckillProductPO();
        BeanUtils.copyProperties(addSeckillProductVO, seckillProductPO);

        // 设置属性
        // 状态为未开始
        seckillProductPO.setProductStatus(SeckillProductConstant.ProductStatus.NOT_START.getCode());
        // 设置创建时间
        seckillProductPO.setCreateDate(new Date());

        // 保存
        seckillProductMapper.insert(seckillProductPO);

        // 添加商品时，将商品的数量以及连接放入redis
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        // 商品数量放入redis
        operations.set(RedisConstant.PRODUCTION_NUMBER + seckillProductPO.getProductId(),
                String.valueOf(seckillProductPO.getProductNumber()));

        // 删除缓存
        redisTemplate.delete(RedisConstant.PRODUCTIONS);

        // redis 中添加 成功与订单集合
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        opsForSet.add(RedisConstant.SET_BUY + seckillProductPO.getProductId(), RedisConstant.SET_DEFAULT_NUMBER);
        opsForSet.add(RedisConstant.SET_ORDER + seckillProductPO.getProductId(), RedisConstant.SET_DEFAULT_NUMBER);

        // 将商品放入消息队列中
        delayedQueueProducerService.addReflashProductMsg(seckillProductPO.getBeginTime(),
                seckillProductPO.getProductId().toString());
    }

    /**
     * 修改产品
     *
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
        if (productNumber != null) {
            redisTemplate.opsForValue().set(RedisConstant.PRODUCTION_NUMBER
                    + seckillProductPO.getProductId(), String.valueOf(productNumber));
        }

        // 从数据库中查出对象
        SeckillProductPO originProduct = seckillProductMapper.selectById(addSeckillProductVO.getProductId());
        if (!originProduct.getBeginTime().equals(seckillProductPO.getBeginTime())){
            // 放入消息队列
            // 将商品放入消息队列中
            delayedQueueProducerService.addReflashProductMsg(seckillProductPO.getBeginTime(),
                    seckillProductPO.getProductId().toString());
        }

        // 更新
        seckillProductMapper.updateById(seckillProductPO);

        // 删除产品信息
        redisTemplate.delete(RedisConstant.PRODUCTIONS);

        this.reflashProduction();


    }

    /**
     * 删除产品
     *
     * @param productIdList id集合
     */
    @Override
    public void deleteSeckillProduct(List<Long> productIdList) {

        if (productIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 删除 redis 中的商品数量
        productIdList.forEach(item -> {
                    redisTemplate.delete(RedisConstant.PRODUCTION_NUMBER + item);
                    // 删除 redis 中的记录
                    redisTemplate.delete(RedisConstant.SET_BUY + item);
                    redisTemplate.delete(RedisConstant.SET_ORDER + item);
                }
        );

        // 删除 redis 中的产品
        redisTemplate.delete(RedisConstant.PRODUCTIONS);


        // 不为空则删除
        seckillProductMapper.deleteBatchIds(productIdList);
    }

    /**
     * 用户取消支付
     *
     * @param request   请求
     * @param productId 商品id
     */
    @Override
    public void disPay(HttpServletRequest request, Long productId) {

        String token = request.getHeader("token");
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        // 删除 redis 缓存
        String msg = userId + "_" + productId;
        Boolean delete = redisTemplate.delete(RedisConstant.ORDER + msg);
        delete = delete != null && delete;

        // 删除 订单记录
        redisTemplate.opsForSet().remove(RedisConstant.SET_ORDER + productId, userId);

        if (!delete) {
            throw new RuntimeException("订单已过期！");
        }


        // 商品数量 +1
        redisTemplate.opsForValue().increment(RedisConstant.PRODUCTION_NUMBER + productId);
    }

    /**
     * 用户付款
     *
     * @param request 请求
     * @param payVO   商品支付封装类
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void pay(HttpServletRequest request, PayVO payVO) {

        String token = request.getHeader("token");
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        // 拿到用户账户
        QueryWrapper<AccountPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(userId));
        AccountPO accountPO = accountMapper.selectOne(queryWrapper);

        // 判断账户和密码是否正确
//        if (!payVO.getAccountNumber().equals(accountPO.getAccountNum())) {
//            throw new RuntimeException(ExceptionConstant.ACCOUNT_FILED);
//        }
        // 支付密码错误
        if (!payVO.getPayPassword().equals(accountPO.getPayPassword())) {
            throw new RuntimeException(ExceptionConstant.PAY_PASSWORD_FILED);
        }

        SeckillResultPO order = getOrder(request, payVO.getProductId());

        if (order == null) {
            throw new RuntimeException("订单已过期！请重新抢购！");
        }

        // 判断余额是否充足
        BigDecimal balance = accountPO.getBalance();
        BigDecimal productPrice = order.getProductPrice();


        // 余额不足
        if (balance.compareTo(productPrice) < 0) {
            throw new RuntimeException(ExceptionConstant.MONEY_IS_NOT_ENOUGH);
        }
        // 更改状态为已支付
        order.setPayStatus(OrderConstant.PayStatus.PAY.getCode());

        // CAS 扣余额
        accountMapper.decBalance(accountPO.getAccountId(), productPrice, accountPO.getBalance());

        // 插入数据库
        // 使用消息队列
//        String orderDetail = JSON.toJSONString(order);
//        writeDBProducerService.writeOrderDetail(orderDetail);
        seckillResultMapper.insert(order);

        // TODO 更新银行余额
        redisTemplate.opsForValue().increment(RedisConstant.BANK_ACCOUNT, order.getProductPrice().doubleValue());

        // set 操作对象
        SetOperations<String, String> forSet = redisTemplate.opsForSet();

        // 从订单 set 中删除
        forSet.remove(RedisConstant.SET_ORDER + order.getProductId(), order.getUserId().toString());
        // 装入已购买的 set 中
        forSet.add(RedisConstant.SET_BUY + order.getProductId(), order.getUserId().toString());

        // 删除 redis 中的 key
        String key = RedisConstant.ORDER + order.getUserId() + "_" + order.getProductId();
        redisTemplate.delete(key);
    }

    /**
     * 拿到用户订单
     *
     * @param request   请求
     * @param productId 商品 id
     * @return 订单
     */
    @Override
    public SeckillResultPO getOrder(HttpServletRequest request, Long productId) {

        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

        String token = request.getHeader("token");
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        String msg = userId + "_" + productId;

        String order = opsForValue.get(RedisConstant.ORDER + msg);

        // 转为订单对象
        SeckillResultPO seckillResultPO = JSON.parseObject(order, SeckillResultPO.class);

        return seckillResultPO;
    }

    /**
     * 判断用户有无未付款的订单
     *
     * @param request 请求
     * @return 未付款的订单
     */
    @Override
    public SeckillResultPO checkOrdered(HttpServletRequest request) {

        // 拿到 操作对象
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

        // 如果为空 则返回空
        Set<String> members = opsForSet.members(RedisConstant.STARTING_PRODUCT);
        if (members == null) return null;

        String token = request.getHeader("token");
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        // 遍历判断
        for (String productId : members) {

            Boolean member = opsForSet.isMember(RedisConstant.SET_ORDER + productId, userId);
            member = member != null && member;

            // 如果有
            if (member) {
                String msg = userId + "_" + productId;
                String order = opsForValue.get(RedisConstant.ORDER + msg);

                if (order != null) {
                    return JSON.parseObject(order, SeckillResultPO.class);
                }
            }
        }
        return null;
    }

    /**
     * 修改链接
     */
    @Override
    public String updateUrl(RandomProductUrlVO randomProductUrlVO) {

        // 拿到数据
        if (randomProductUrlVO.getLength() == null) randomProductUrlVO.setLength(50);
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
        String urlFromRedis = operations.get(RedisConstant.PRODUCTION_URL
                + randomProductUrlVO.getProductId());

        // 将连接写入redis
        if (urlFromRedis != null) {
            // 如果redis中有连接，则更新，若无则等待统一更新
            operations.set(RedisConstant.PRODUCTION_URL + randomProductUrlVO.getProductId(), url,
                    RedisConstant.PRODUCTION_URL_EXPIRED_TIME, TimeUnit.MINUTES);
        }

        // 更新链接
        seckillProductMapper.updateById(seckillProductPO);
        return url;
    }

    /**
     * 批量修改活动链接
     * @param voList 实体
     */
    @Override
    public void updateUrlBatch(List<RandomProductUrlVO> voList) {

        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

        List<SeckillProductPO> collect = voList.stream().map(item -> {
            // 拿到 pid
            Long productId = item.getProductId();
            // 拿到 len 并生成随机链接
            Integer length = item.getLength();
            String randomUrl = ProductUtil.createRandomUrl(length);

            // 在 redis 中更新
            opsForValue.set(RedisConstant.PRODUCTION_URL + productId, randomUrl,
                    RedisConstant.PRODUCTION_URL_EXPIRED_TIME, TimeUnit.MINUTES);

            // 创建对象 设置属性
            SeckillProductPO productPO = new SeckillProductPO();
            productPO.setProductId(productId);
            productPO.setUrl(randomUrl);
            return productPO;
        }).collect(Collectors.toList());

        this.updateBatchById(collect);
    }

    /**
     * 刷新产品
     */
    @Override
    public void reflashProduction() {

        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();

        // 查缓存 如果有变化则放入数据库
        List<SeckillProductPO> seckillProductPOS = this.showProductions();

        // 有无改动的 flag
        AtomicBoolean flag = new AtomicBoolean(false);

        // 过滤数据 修改数据
        List<SeckillProductPO> collect = seckillProductPOS.stream().map(item -> {
            // 创建实体类
            SeckillProductPO seckillProductPO = new SeckillProductPO();
            // 复制属性
            BeanUtils.copyProperties(item, seckillProductPO);
            // 拿到 当前时间 和 产品开始 以及 结束时间
            Date currentTime = new Date();
            Date beginTime = seckillProductPO.getBeginTime();
            Date endTime = seckillProductPO.getEndTime();

            // 如果当前时间大于 结束时间
            if (currentTime.after(endTime)) {
                // 设置为已结束
                seckillProductPO.setProductStatus(SeckillProductConstant.ProductStatus.FINISHED.getCode());
                // TODO 删除 redis 缓存
                redisTemplate.delete(RedisConstant.SET_BUY + item.getProductId());
                redisTemplate.delete(RedisConstant.SET_ORDER + item.getProductId());
                flag.set(true);
                opsForSet.remove(RedisConstant.STARTING_PRODUCT, item.getProductId().toString());
            } else if (currentTime.before(beginTime)) { // 当前时间 在 开始时间之前
                // 设置为 未开始
                seckillProductPO.setProductStatus(SeckillProductConstant.ProductStatus.NOT_START.getCode());
                flag.set(true);
                opsForSet.remove(RedisConstant.STARTING_PRODUCT, item.getProductId().toString());
            } else { // 当前时间 在 开始时间之后 结束时间之前
                // 设置为 正在进行
                seckillProductPO.setProductStatus(SeckillProductConstant.ProductStatus.START_ING.getCode());
                // TODO 将正在开始的活动 id 放入 redis
                opsForSet.add(RedisConstant.STARTING_PRODUCT, item.getProductId().toString());
                flag.set(true);
            }
            return seckillProductPO;
        }).collect(Collectors.toList());

        // 批量修改
        if (flag.get()) this.updateBatchById(collect);
    }

    /**
     * 周期刷新正在开始的活动的 url
     */
    @Override
    public void reflashProductUrl() {
        // 从 redis 中拿到正在开始的活动
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        Set<String> members = opsForSet.members(RedisConstant.STARTING_PRODUCT);

        if (members != null) {
            // 刷新活动时间
            // url 默认长度 为 50
            members.forEach(item -> this.updateUrl(new RandomProductUrlVO(Long.parseLong(item),
                    SeckillProductConstant.URL_DEFAULT_LEN)));
        }
    }

    /**
     * 从redis中获取产品数量
     *
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
     *
     * @return 商品集合
     */
    @Override
    public List<SeckillProductPO> showProductions() {

        // 先从redis中获取
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String stringOProductions = operations.get(RedisConstant.PRODUCTIONS);

        if (stringOProductions == null) {
            // 加锁
            synchronized (this) {
                // 再次查一遍redis
                String stringOProductions2 = operations.get(RedisConstant.PRODUCTIONS);

                // 判断
                if (stringOProductions2 == null) {
                    // 查数据库
                    QueryWrapper<SeckillProductPO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("show_status", "0");
                    queryWrapper.orderByAsc("begin_time");
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
                        new TypeReference<List<SeckillProductPO>>() {
                        });
            }
        }
        // 如果不为空
        return JSON.parseObject(stringOProductions,
                new TypeReference<List<SeckillProductPO>>() {
                });
    }

    /**
     * 进行秒杀(事务方法)
     *
     * @param dynamicUrl1  动态链接1
     * @param dynamicUrl12 动态链接1
     * @param mySeckillVO  秒杀信息封装
     * @param request      用户请求
     * @return 秒杀结果
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public String doSeckill(String dynamicUrl1, String dynamicUrl12, MySeckillVO mySeckillVO,
                            HttpServletRequest request) {

        // 拿到 redis 的操作对象
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // 1、url 判断
//        boolean urlJudge = urlJudge(operations, mySeckillVO, dynamicUrl1, dynamicUrl12);
//        if (!urlJudge)
//            throw new RuntimeException(SeckillProductConstant.URL_FILED);

        // 2、判断活动有无在开始集合中
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        Boolean member = opsForSet.isMember(RedisConstant.STARTING_PRODUCT,
                mySeckillVO.getProductId().toString());
        // 判断 member 是否为空
        member = member != null && member;
        if (!member) {
            throw new RuntimeException(ExceptionConstant.PRODUCT_NOT_START_EXCEPTION);
        }

        // 拿到辅助字符串
        String token = request.getHeader("token");
        String userId = JWTUtils.verify(token).getClaim("userId").asString();
        String hasPermission = JWTUtils.verify(token).getClaim("hasPermission").asString();

        // 3、判断用户购买权限
        if ("false".equals(hasPermission)) {
            throw new RuntimeException(SeckillProductConstant.PERMISSION_FILED);
        }

        // 4、流量控制 保证每人 10s 中之内只能点击一次链接
        flowControl(operations, Long.parseLong(userId));

        // 5、判断用户是否重复购买
        judgeBuy(mySeckillVO, userId);

        // 6、用户账户判断
        accountJudge(mySeckillVO, request);

        // 7、进行产品秒杀
        boolean doingSeckill = doingSeckill3(redisTemplate, mySeckillVO, request);

        // 返回抢购信息
        return doingSeckill ? SeckillProductConstant.PRODUCTION_SUCCESS
                : SeckillProductConstant.PRODUCTION_FILED;
    }

    /****************************************************************************************************
     ***********************************************  方法  **********************************************
     *****************************************************************************************************/

    /* 判断url */
    public boolean urlJudge(ValueOperations<String, String> operations, MySeckillVO mySeckillVO,
                            String dynamicUrl1, String dynamicUrl2) {

        // 判断动态链接是否为空
        if (dynamicUrl1 == null || dynamicUrl2 == null)
            throw new RuntimeException(SeckillProductConstant.URL_NULL);

        // 拼接动态链接
        String dynamicUrl = SeckillProductConstant.PRODUCT_ACTIVITY_URL + dynamicUrl1 + "/" + dynamicUrl2;

        // 拿到产品id
        Long productId = mySeckillVO.getProductId();
        String productionUrl = operations.get(RedisConstant.PRODUCTION_URL + productId);

        // 如果链接为空
        if (productionUrl == null) {
            // 上锁
            synchronized (this) {
                String productionUrl2 = operations.get(RedisConstant.PRODUCTION_URL + productId);
                if (productionUrl2 == null) {
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

    /* 判断账户信息是否正确 */
    public void accountJudge(MySeckillVO mySeckillVO, HttpServletRequest request) {
        // 拿到当前用户id
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        String userId = verify.getClaim("userId").asString();

        // 拿到用户的账户
        QueryWrapper<AccountPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(userId));
        AccountPO accountPO = accountMapper.selectOne(queryWrapper);

        // 判断用户账户是否存在
        if (accountPO == null) {
            throw new RuntimeException("当前用户未创建账户");
        }
    }

    /* 流量控制 保证用户在 10s 之内只能点击一次秒杀 */
    public void flowControl(ValueOperations<String, String> operations, Long userId) {

        // 是否点击过
        String hit = operations.get(userId + RedisConstant.HIT);

        if (hit != null) { // 点击过
            // 10s 中之内请勿多次点击购买
            throw new RuntimeException(ExceptionConstant.HIT_MANY_TIMES_EXCEPTION);
        } else { // 未点击过
            synchronized (this) {
                // 再次判断
                String hit2 = operations.get(userId + RedisConstant.HIT);

                if (hit2 != null) {
                    // 10s 中之内请勿多次点击购买
                    throw new RuntimeException(ExceptionConstant.HIT_MANY_TIMES_EXCEPTION);
                }
                // 用户点击记录放入 redis 中，过期时间 10s
                operations.set(userId + RedisConstant.HIT, "1", 10L, TimeUnit.SECONDS);
            }
        }
    }

    /* 判断用户是否重复购买 */
    public void judgeBuy(MySeckillVO mySeckillVO, String userId) {

        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();

        Boolean isOrder = opsForSet.isMember(RedisConstant.SET_ORDER + mySeckillVO.getProductId(), userId);

        Boolean isBuying = opsForSet.isMember(RedisConstant.SET_BUY + mySeckillVO.getProductId(), userId);

        isOrder = isOrder != null && isOrder;
        isBuying = isBuying != null && isBuying;

        if (isBuying || isOrder)
            throw new RuntimeException(ExceptionConstant.BUY_MORE_THEN_ONE);
    }

    /* 进行秒杀 -- script */
    private boolean doingSeckill(StringRedisTemplate redisTemplate, MySeckillVO mySeckillVO,
                                 HttpServletRequest request, AccountPO accountPO) {

        // 拿到产品id
        Long productId = mySeckillVO.getProductId();
        // 拿到当前用户id 和 姓名
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        String userId = verify.getClaim("userId").asString();
        String userName = verify.getClaim("userName").asString();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // 拿到商品信息
        SeckillProductPO seckillProductPO = seckillProductMapper.selectById(productId);
//        SeckillProductPO seckillProductPO = new SeckillProductPO();
//        seckillProductPO.setProductPrice(new BigDecimal(1000));

        // 使用 lua 脚本减库存
        boolean stock = delStockByLua2(redisTemplate, productId);
        if (stock) {
            // TODO 判断用户余额是否充足
            BigDecimal balance = accountPO.getBalance();
            BigDecimal price = seckillProductPO.getProductPrice();
            if (balance.compareTo(price) < 0) {
                // 库存恢复
                operations.increment(RedisConstant.PRODUCTION_NUMBER + productId);
                throw new RuntimeException("账户余额不足");
            }

            // 扣用户余额
            boolean b = accountMapper.decBalance(accountPO.getAccountId(), price, balance);
            if (!b) { // 余额扣除失败
                operations.increment(RedisConstant.PRODUCTION_NUMBER + productId);
                throw new RuntimeException("秒杀失败，请重试！");
            }
        }

        // 生成订单对象
        SeckillResultPO seckillResultPO = new SeckillResultPO();
        BeanUtils.copyProperties(mySeckillVO, seckillResultPO);
        seckillResultPO.setCreateDate(new Date());
        // 用户id和姓名放入对象
        seckillResultPO.setUserId(Long.parseLong(userId));
        seckillResultPO.setUserName(userName);
        // 将产品价格存入
        seckillResultPO.setProductPrice(seckillProductPO.getProductPrice());

        // 存入数据
        try {
            seckillResultMapper.insert(seckillResultPO);
        } catch (DuplicateKeyException e) {
            operations.increment(RedisConstant.PRODUCTION_NUMBER + productId);
            throw new SQLDuplicateException("不能重复购买");
        }
        return true;
    }

    /* 进行秒杀 -- sync */
    private boolean doingSeckill2(StringRedisTemplate redisTemplate, MySeckillVO mySeckillVO,
                                  HttpServletRequest request, AccountPO accountPO) {

        // 拿到产品id
        Long productId = mySeckillVO.getProductId();
        // 拿到当前用户id 和 姓名
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        String userId = verify.getClaim("userId").asString();
        String userName = verify.getClaim("userName").asString();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // 拿到商品信息
        List<SeckillProductPO> seckillProductPOS = this.showProductions();
        // 找到我们需要的
        SeckillProductPO seckillProductPO = new SeckillProductPO();
        // 遍历商品列表
        for (SeckillProductPO item : seckillProductPOS) {
            if (item.getProductId().equals(productId)) {
                BeanUtils.copyProperties(item, seckillProductPO);
                break;
            }
        }
        // 上锁
        synchronized (this) {

            // 从redis中拿到产品数量
            String number = operations.get(RedisConstant.PRODUCTION_NUMBER + productId);
            number = number == null ? "-1" : number;

            if (Integer.parseInt(number) <= 0) {
                // 数据库更新
                seckillProductMapper.letProductionZero(productId);
                return false;
            }

            // TODO 判断用户余额是否充足
            BigDecimal balance = accountPO.getBalance();
            BigDecimal price = seckillProductPO.getProductPrice();
            if (balance.compareTo(price) < 0) {
                // 库存恢复
                throw new RuntimeException("账户余额不足");
            }

            // 扣用户余额
            boolean b = accountMapper.decBalance(accountPO.getAccountId(), price, balance);
            if (b) { // 余额扣除成功
                // 扣除 redis 库存
                operations.decrement(RedisConstant.PRODUCTION_NUMBER + productId);
            } else { // 失败
                throw new RuntimeException("秒杀失败，请重试！");
            }
        }

        // 生成订单对象
        SeckillResultPO seckillResultPO = new SeckillResultPO();
        BeanUtils.copyProperties(mySeckillVO, seckillResultPO);
        seckillResultPO.setCreateDate(new Date());
        // 用户id和姓名放入对象
        seckillResultPO.setUserId(Long.parseLong(userId));
        seckillResultPO.setUserName(userName);
        // 将产品价格存入
        seckillResultPO.setProductPrice(seckillProductPO.getProductPrice());

        // 存入数据
        try {
            seckillResultMapper.insert(seckillResultPO);
        } catch (DuplicateKeyException e) {
            // 返回库存
            operations.increment(RedisConstant.PRODUCTION_NUMBER + productId);
            throw new SQLDuplicateException("不能重复购买");
        }
        return true;
    }

    /* 进行秒杀 -- mq订单 */
    private boolean doingSeckill3(StringRedisTemplate redisTemplate, MySeckillVO mySeckillVO,
                                  HttpServletRequest request) {

        // 拿到产品id
        Long productId = mySeckillVO.getProductId();
        // 拿到当前用户id 和 姓名
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        String userId = verify.getClaim("userId").asString();
        String userName = verify.getClaim("userName").asString();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // 拿到商品信息
        List<SeckillProductPO> seckillProductPOS = this.showProductions();
        // 找到我们需要的
        SeckillProductPO seckillProductPO = new SeckillProductPO();
        // 遍历商品列表
        for (SeckillProductPO item : seckillProductPOS) {
            if (item.getProductId().equals(productId)) {
                BeanUtils.copyProperties(item, seckillProductPO);
                break;
            }
        }

        // 上锁
        // 从redis中拿到产品数量
        String number1 = operations.get(RedisConstant.PRODUCTION_NUMBER + productId);
        number1 = number1 == null ? "-1" : number1;
        if (Integer.parseInt(number1) == 0) return false;

        synchronized (this) {
            // 从redis中拿到产品数量
            String number = operations.get(RedisConstant.PRODUCTION_NUMBER + productId);
            number = number == null ? "-1" : number;

            if (Integer.parseInt(number) <= 0) {
                // 数据库更新
                return false;
            }

            // 扣减库存
            operations.decrement(RedisConstant.PRODUCTION_NUMBER + productId);
        }

        // 生成订单对象
        SeckillResultPO seckillResultPO = createOrder(mySeckillVO, userId, userName, seckillProductPO);

        // 将订单存入 redis
        try {
            // 将订单对象转为 json 字符串
            String msg = seckillResultPO.getUserId() + "_" + seckillResultPO.getProductId();
            String orderString = JSON.toJSONString(seckillResultPO);

            // 测试 62 s 过期
//            operations.set(RedisConstant.ORDER + msg, orderString, RedisConstant.ORDER_EXPIRE_TIME, TimeUnit.SECONDS);
            // 将订单信息放入 redis 并设置 12 分钟过期
            operations.set(RedisConstant.ORDER + msg, orderString, RedisConstant.ORDER_EXPIRE_TIME, TimeUnit.MINUTES);
            // 用户 id 放入产品订单集合中
            redisTemplate.opsForSet().add(RedisConstant.SET_ORDER + productId, userId);

            // 生成消息
            delayedQueueProducerService.createOrderMsg(msg);
        } catch (Exception e) {
            // 返回库存
            operations.increment(RedisConstant.PRODUCTION_NUMBER + productId);
            throw new SQLDuplicateException("生成订单失败");
        }
        return true;
    }

    // 生产订单对象
    public SeckillResultPO createOrder(MySeckillVO mySeckillVO, String userId, String userName, SeckillProductPO
            seckillProductPO) {
        SeckillResultPO seckillResultPO = new SeckillResultPO();
        BeanUtils.copyProperties(mySeckillVO, seckillResultPO);
        seckillResultPO.setCreateDate(new Date());
        // 用户id和姓名放入对象
        seckillResultPO.setUserId(Long.parseLong(userId));
        seckillResultPO.setUserName(userName);
        // 将产品价格存入
        seckillResultPO.setProductPrice(seckillProductPO.getProductPrice());
        // 设置订单号
        // 拿到当前时间
        Date date = new Date();
        String orderId = DateUtil.format(date, "yyyyMMddHHmmss");
        String uuid = IdUtil.simpleUUID().substring(0, 5);
        seckillResultPO.setOrderId(orderId.concat(uuid));
        // 设置订单状态为 未支付
        seckillResultPO.setPayStatus(OrderConstant.PayStatus.NOT_PAY.getCode());
        return seckillResultPO;
    }

    // 脚本减库存操作
    public boolean delStockByLua(StringRedisTemplate redisTemplate, Long productId) {
        String key = RedisConstant.PRODUCTION_NUMBER + productId;

        // 参数 KEYS
        List<String> keys = new ArrayList<>();
        keys.add(key);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(RedisConstant.luo_sub_stock, Long.class);

        Long execute = redisTemplate.execute(script, keys, "1");
        execute = execute == null ? -1 : execute;

        // 2 的时候为 true
        return execute == 2;
    }

    // 脚本减库存操作
    public boolean delStockByLua2(StringRedisTemplate redisTemplate, Long productId) {
        String key = RedisConstant.PRODUCTION_NUMBER + productId;

        // 参数 KEYS
//        List<byte[]> keys = new ArrayList<>();
//        keys.add(key.getBytes(StandardCharsets.UTF_8));

        List<byte[]> args = new ArrayList<>();
        args.add("1".getBytes(StandardCharsets.UTF_8));

        Long execute = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                Object nativeConnection = connection.getNativeConnection();
                Object o = ((JedisClusterConnection) nativeConnection).execute(RedisConstant.luo_sub_stock, key.getBytes(StandardCharsets.UTF_8), args);
                o = o == null ? "-1" : o;
                return Long.parseLong(o.toString());
            }
        });
        execute = execute == null ? -1 : execute;

        // 2 的时候为 true
        return execute == 2;
    }

    /* 把url存入redis */
    public void letUrlIntoRedis(ValueOperations<String, String> operations) {
        QueryWrapper<SeckillProductPO> queryWrapper = new QueryWrapper<>();
        List<SeckillProductPO> seckillProductPOS = seckillProductMapper.selectList(queryWrapper);
        // 放入redis 过期时间为 20 分钟
        seckillProductPOS.forEach(item -> {
            operations.set(RedisConstant.PRODUCTION_URL + item.getProductId(), item.getUrl(),
                    RedisConstant.PRODUCTION_URL_EXPIRED_TIME, TimeUnit.MINUTES);
        });
    }
}
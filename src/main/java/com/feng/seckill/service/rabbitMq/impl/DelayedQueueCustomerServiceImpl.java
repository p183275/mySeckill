package com.feng.seckill.service.rabbitMq.impl;

import com.alibaba.fastjson.JSONObject;
import com.feng.seckill.config.rabbitmq.DelayedQueueConfig;
import com.feng.seckill.config.rabbitmq.OrderQueueConfig;
import com.feng.seckill.entitys.constant.OrderConstant;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.vo.RandomProductUrlVO;
import com.feng.seckill.service.SeckillProductService;
import com.feng.seckill.service.rabbitMq.DelayedQueueProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/3/28 16:44
 */
@Slf4j
@Service
public class DelayedQueueCustomerServiceImpl {

    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private DelayedQueueProducerService producerService;

    /**
     * 监听商品刷新的消息
     * @param message 消息体
     */
    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
    public void receiveProductionDelayedQueue(Message message){

        try {

        // 拿到消息中包含的 产品 id
        String msg = new String(message.getBody());

        // 拿到对应商品的信息
        SeckillProductPO byId = seckillProductService.getById(Long.parseLong(msg));

        // 判断结束时间是否再此之前
        Date instance = Calendar.getInstance().getTime();

        // 判断当前是否属于商品开始时的刷新
        if (instance.before(byId.getEndTime())){

            // 活动开始时候刷新商品的 url
            seckillProductService.updateUrl(new RandomProductUrlVO(Long.parseLong(msg), 50));

            // 增加一个商品结束的延迟任务
            producerService.addReflashProductMsg(byId.getEndTime(), msg);
        }

        // 调用商品刷新
        seckillProductService.reflashProduction();
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    @RabbitListener(queues = OrderQueueConfig.DELAYED_ORDER_QUEUE)
    public void judgeOrderIsPay(Message message){

        try {

            // 角色id_商品id
            String msg = new String(message.getBody());

            // 从 redis 中拿到订单
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            String orderMsg = operations.get(RedisConstant.ORDER + msg);

            if (orderMsg != null) {
                SeckillResultPO seckillResultPO = JSONObject.parseObject(orderMsg, SeckillResultPO.class);
                // 判断此时状态如果为 未支付
                if (OrderConstant.PayStatus.NOT_PAY.getCode().equals(seckillResultPO.getPayStatus())) {
                    // 拿到商品 id
                    // userId_ProductId
                    String[] split = msg.split("_");
                    long productId = Long.parseLong(split[1]);
                    // 恢复 redis 中的库存
                    operations.increment(RedisConstant.PRODUCTION_NUMBER + productId);

//                System.out.println("时间点到 删除订单" + msg);

                    // TODO 删除订单数据
//                 删除 用户购买记录
                    redisTemplate.opsForSet().remove(RedisConstant.SET_ORDER + productId, split[0]);
                    redisTemplate.delete(RedisConstant.ORDER + msg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}

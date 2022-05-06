package com.feng.seckill.service.rabbitMq.impl;

import com.feng.seckill.config.rabbitmq.DelayedQueueConfig;
import com.feng.seckill.config.rabbitmq.OrderQueueConfig;
import com.feng.seckill.entitys.constant.RabbitMqConstant;
import com.feng.seckill.service.rabbitMq.DelayedQueueProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/3/28 16:43
 */
@Slf4j
@Service
public class DelayedQueueProducerServiceImpl implements DelayedQueueProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 添加刷新活动的延迟队列
     */
    @Override
    public void addReflashProductMsg(Date time, String message) {
        // 拿到当前时间
        Calendar instance = Calendar.getInstance();
        long curTimeInMillis = instance.getTimeInMillis();
        long startTimeInMillis = time.getTime();

//        log.info("商品{}，放入延迟队列", message);

        // 生成两个延迟消息队列
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME,
                DelayedQueueConfig.DELAYED_ROUTING_KEY, message, msg -> {
                    msg.getMessageProperties().setDelay((int) (startTimeInMillis - curTimeInMillis));
                    return msg;
                });
    }

    /**
     * 创建订单消息
     *
     * @param message 消息
     */
    @Override
    public void createOrderMsg(String message) {
//        log.info("用户{}生成订单，放入延迟队列", message);
//        System.out.println("生成消息" + message);
        rabbitTemplate.convertAndSend(OrderQueueConfig.DELAYED_ORDER_EXCHANGE,
                OrderQueueConfig.DELAYED_ORDER_ROUTING_KEY, message, msg -> {
                    // 设置过期时间为 10 分钟
                    msg.getMessageProperties().setDelay(RabbitMqConstant.ORDER_EXPIRE_TIME);
                    return msg;
                });
    }
}

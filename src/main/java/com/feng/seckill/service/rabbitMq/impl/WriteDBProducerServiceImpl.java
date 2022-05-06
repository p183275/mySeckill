package com.feng.seckill.service.rabbitMq.impl;

import com.feng.seckill.config.rabbitmq.AsyncOPConfig;
import com.feng.seckill.service.rabbitMq.WriteDBProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : pcf
 * @date : 2022/4/13 12:58
 */
@Slf4j
@Service
public class WriteDBProducerServiceImpl implements WriteDBProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void writeFirstFilter(Long userId, String userName, String passStatus) {

        // 将三个信息拼接起来
        String msg = userId + "_" + userName + "_" + passStatus;

//        log.info("生成初筛消息");

        // 发送消息
        rabbitTemplate.convertAndSend(AsyncOPConfig.NORMAL_EXCHANGE, AsyncOPConfig.FILTER_ROUTING_KEY,
                msg);
    }

    @Override
    public void writeOrderDetail(String orderDetail) {
        // 发送消息
        rabbitTemplate.convertAndSend(AsyncOPConfig.NORMAL_EXCHANGE, AsyncOPConfig.RESULT_ROUTING_KEY,
                orderDetail);
    }
}

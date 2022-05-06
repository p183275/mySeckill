package com.feng.seckill.service.rabbitMq.impl;

import com.alibaba.fastjson.JSON;
import com.feng.seckill.config.rabbitmq.AsyncOPConfig;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.mapper.SeckillResultMapper;
import com.feng.seckill.service.FirstFilterService;
import javafx.geometry.VPos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : pcf
 * @date : 2022/4/13 13:06
 */
@Slf4j
@Service
public class WriteDBConsumerServiceImpl {

    @Autowired
    private FirstFilterService firstFilterService;
    @Autowired
    private SeckillResultMapper seckillResultMapper;

    @RabbitListener(queues = AsyncOPConfig.FIRST_FILTER_QUEUE)
    public void dealFilterMsg(Message message){
        try {

            String msg = new String(message.getBody());

            // 将属性分隔 String msg = userId + "_" + userName + "_" + passStatus;
            String[] strings = msg.split("_");

            // 初筛信息入库
//        log.info("初筛信息入库，消息{}", msg);
            firstFilterService.addRecordsByUser(Long.parseLong(strings[0]), strings[1], strings[2]);
//        log.info("初筛信息入库成功");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = AsyncOPConfig.RESULT_QUEUE)
    public void dealResult(Message message){
        String msg = new String(message.getBody());

        // 从 json 字符串转回
        SeckillResultPO order = JSON.parseObject(msg, SeckillResultPO.class);
//        log.info("秒杀结果入库成功{}", order);
        seckillResultMapper.insert(order);
    }

}

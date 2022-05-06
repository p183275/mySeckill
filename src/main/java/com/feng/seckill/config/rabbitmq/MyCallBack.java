package com.feng.seckill.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author : pcf
 * @date : 2022/3/28 16:39
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    // 注入
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 注入
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 交换机进行回调的方法
     * @param correlationData 回调消息的id及相关信息
     * @param b 交换机收到消息 true
     * @param s null
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        String id = correlationData != null ? correlationData.getId() : "";
//        if (b){
//            log.info("交换机收到了ID为{}的消息", id);
//        }else {
//            log.info("交换机未收到ID为{}的消息，原因{}", id, s);
//        }
    }

    /**
     * 消息不可达到目的地的时候进行回退
     * @param message 消息
     * @param replyCode 错误代码
     * @param replyText 错误原因
     * @param exchange 交换机
     * @param routingKey routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText,
                                String exchange, String routingKey) {
//        log.info("消息{}, 被交换机{}退回，错误原因{}，routingKey{}", new String(message.getBody()),
//                exchange, replyText, routingKey);
    }
}

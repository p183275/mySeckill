package com.feng.seckill.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/3/28 19:59
 */
@Configuration
public class OrderQueueConfig {

    // 交换机
    public static final String DELAYED_ORDER_EXCHANGE = "order.exchange";

    // 队列
    public static final String DELAYED_ORDER_QUEUE = "order.queue";

    // routingKey
    public static final String DELAYED_ORDER_ROUTING_KEY = "order.routingKey";

    // 声明队列
    @Bean
    public Queue orderQueue(){
        return new Queue(DELAYED_ORDER_QUEUE);
    }

    // 声明交换机
    @Bean
    public CustomExchange orderExchange(){
        String exchangeType = "x-delayed-message";
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        /**
         * 1、交换机名称
         * 2、交换机类型
         * 3、是否持久化
         * 4、是否自动删除
         * 5、配置map
         */
        return new CustomExchange(DELAYED_ORDER_EXCHANGE, exchangeType,
                true, false, arguments);
    }

    // 绑定
    @Bean
    public Binding orderExchangeAndQueue(@Qualifier("orderQueue") Queue orderQueue,
                                         @Qualifier("orderExchange") CustomExchange orderExchange){
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(DELAYED_ORDER_ROUTING_KEY).noargs();
    }
}

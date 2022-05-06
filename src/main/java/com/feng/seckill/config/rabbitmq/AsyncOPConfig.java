package com.feng.seckill.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : pcf
 * @date : 2022/4/13 12:39
 * 声明普通交换机
 */
@Configuration
public class AsyncOPConfig {

    // 声明普通交换机
    public static final String NORMAL_EXCHANGE = "NOR_EX";
    // 声明初筛信息入库队列
    public static final String FIRST_FILTER_QUEUE = "FIRST_FILTER_QUEUE";
    // 设置 routingKey
    public static final String FILTER_ROUTING_KEY = "FILTER";
    // 声明支付结果入库队列
    public static final String RESULT_QUEUE = "RESULT_QUEUE";
    // 设置 routingkey
    public static final String RESULT_ROUTING_KEY = "RESULT";

    @Bean("norExchange")
    public DirectExchange normal(){
        return new DirectExchange(NORMAL_EXCHANGE);
    }

    @Bean(value = "filterQueue")
    public Queue filterQueue(){
        return new Queue(FIRST_FILTER_QUEUE);
    }

    @Bean("resultQueue")
    public Queue resultQueue(){
        return new Queue(RESULT_QUEUE);
    }

    // 声明绑定关系
    @Bean
    public Binding binding1(@Qualifier("norExchange") DirectExchange exchange,
                           @Qualifier("filterQueue") Queue filterQueue){
        return BindingBuilder.bind(filterQueue).to(exchange).with(FILTER_ROUTING_KEY);
    }

    @Bean
    public Binding binding2(@Qualifier("norExchange") DirectExchange exchange,
                           @Qualifier("resultQueue") Queue resultQueue){
        return BindingBuilder.bind(resultQueue).to(exchange).with(RESULT_ROUTING_KEY);
    }

}

package com.feng.seckill.service.rabbitMq;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/3/28 16:43
 */
public interface DelayedQueueProducerService {

    /**
     * 添加刷新活动的延迟队列
     */
    void addReflashProductMsg(Date time, String message);

    /**
     * 创建订单消息
     * @param message 消息
     */
    void createOrderMsg(String message);

}

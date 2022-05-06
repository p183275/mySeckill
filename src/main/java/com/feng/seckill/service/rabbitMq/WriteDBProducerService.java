package com.feng.seckill.service.rabbitMq;

/**
 * @author : pcf
 * @date : 2022/4/13 12:57
 */
public interface WriteDBProducerService {

    /**
     * 初筛信息入库
     * @param userId 用户 id
     * @param userName 用户名
     * @param passStatus 标志
     */
    void writeFirstFilter(Long userId, String userName, String passStatus);

    /**
     * 订单信息入库
     * @param orderDetail 订单信息的 json 格式
     */
    void writeOrderDetail(String orderDetail);

}

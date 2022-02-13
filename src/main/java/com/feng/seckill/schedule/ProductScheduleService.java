package com.feng.seckill.schedule;

import com.feng.seckill.service.SeckillProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 商品定时任务类
 * @author : pcf
 * @date : 2022/2/9 18:38
 */
@Service
@Transactional
public class ProductScheduleService {

    @Autowired
    private SeckillProductService seckillProductService;

    /**
     * 定时更新产品数量
     */
    @Scheduled(cron = "0/1 * * * * ? ")
    public void reflashProductNumSchedule(){
        System.out.println("更新产品数量");
        seckillProductService.reflashProductionNumber();
    }

}


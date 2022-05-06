package com.feng.seckill.schedule;

import com.feng.seckill.service.PersonalService;
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
    private PersonalService personalService;

    @Scheduled(cron = "0 50 23 ? * *")
    public void reflashBlackTable(){
        personalService.reflashBlackNames();
    }

}


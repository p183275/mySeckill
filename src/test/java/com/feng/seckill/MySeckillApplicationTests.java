package com.feng.seckill;

import cn.hutool.core.date.DateUtil;
import com.feng.seckill.component.SmsComponent;
import com.feng.seckill.controller.BreakRuleController;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.vo.UserInfoAndAccountVO;
import com.feng.seckill.mapper.UserInfoMapper;
import com.feng.seckill.security.Md5PassEncoder;
import com.feng.seckill.service.PersonalService;
import com.feng.seckill.service.SeckillProductService;
import com.feng.seckill.service.rabbitMq.DelayedQueueProducerService;
import javafx.geometry.VPos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@SpringBootTest
class MySeckillApplicationTests {

//    @Autowired
//    private UserInfoMapper userInfoMapper;
//    @Autowired
//    BreakRuleController breakRuleController;
//    @Autowired
//    SmsComponent smsComponent;
//    @Autowired
//    private DelayedQueueProducerService delayedQueueProducerService;
//    @Autowired
//    private SeckillProductService seckillProductService;
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//
//    @Test
//    public void write() throws IOException {
//        System.out.println("开始写入");
//        File file = new File("C:\\Users\\HP\\Desktop\\login.csv");
//        FileWriter out= new FileWriter (file);
//        BufferedWriter bw= new BufferedWriter(out);
//        try{
//            for (int i = 1; i <= 100000; i++){
//                bw.write(i + "," + "123456");
//                bw.newLine();
//                bw.flush();
//            }
//        }catch(IOException e) {
//            e.printStackTrace();
//        }finally {
//            bw.close();
//            System.out.println("写入完成");
//        }
//    }

//    @Test
//    void testOrderId(){
//        // 拿到当前时间
//        Date date = new Date();
//        String s = DateUtil.format(date, "yyyyMMddHHmmss");
//        System.out.println(s);
//    }
//
//    @Test
//    void redis(){
//        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
//        for (int i = 1; i <= 15; i++){
//            opsForSet.add(RedisConstant.SET_ORDER + i, "0");
//            opsForSet.add(RedisConstant.SET_BUY + i, "0");
//        }
//    }
//
//    @Test
//    void testMq(){
//        Date date = new Date();
//        delayedQueueProducerService.addReflashProductMsg(date, "1");
//    }
//
//    @Test
//    void testSms(){
//        smsComponent.sendSmsCode("15570452082", "1234", "3");
//    }
//
//    @Test
//    void contextLoads() {
//        UserInfoAndAccountVO userInfoAndAccountVO = userInfoMapper.getUserInfoAndAccountVO(1L);
//        System.out.println(userInfoAndAccountVO);
//    }

}

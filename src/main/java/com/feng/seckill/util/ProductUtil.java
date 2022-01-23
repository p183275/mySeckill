package com.feng.seckill.util;

import cn.hutool.core.util.RandomUtil;
import com.feng.seckill.entitys.constant.SeckillProductConstant;

/**
 * @author : pcf
 * @date : 2022/1/16 17:57
 */
public class ProductUtil {

    public static String createRandomUrl(Integer length){

        String s1;
        String s2;

        if (length <= 30){
            int c = RandomUtil.randomInt(10, length * 2 / 3);
            s1 = RandomUtil.randomString(c);
            s2 = RandomUtil.randomString(length - c);
        }else if (length <= 50){
            int c = RandomUtil.randomInt(15, length * 4 / 5);
            s1 = RandomUtil.randomString(c);
            s2 = RandomUtil.randomString(length - c);
        }else if (length <= 100){
            int c = RandomUtil.randomInt(20, length * 9 / 10);
            s1 = RandomUtil.randomString(c);
            s2 = RandomUtil.randomString(length - c);
        }else if (length <= 150){
            int c = RandomUtil.randomInt(25, length * 15 / 15);
            s1 = RandomUtil.randomString(c);
            s2 = RandomUtil.randomString(length - c);
        }else{
            int c = RandomUtil.randomInt(30, length * 19 / 20);
            s1 = RandomUtil.randomString(c);
            s2 = RandomUtil.randomString(length - c);
        }

        String url_tail = s1 + "/" + s2;
        return SeckillProductConstant.PRODUCT_ACTIVITY_URL + url_tail;
    }

}

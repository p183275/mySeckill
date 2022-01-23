package com.feng.seckill.util;

import cn.hutool.core.util.NumberUtil;

/**
 * @author : pcf
 * @date : 2022/1/15 17:00
 */
public class RegisterUtil {

    /**
     * 生成验证码
     * @return 返回生成的验证码
     */
    public static String getCheckCode() {

        // 使用糊涂工具生成验证码
        int[] numbers = NumberUtil.generateRandomNumber(1000, 9999, 1);
        return numbers[0] + "";
    }

}

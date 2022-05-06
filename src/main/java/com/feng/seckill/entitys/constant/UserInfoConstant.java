package com.feng.seckill.entitys.constant;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.math.BigDecimal;

/**
 * @author : pcf
 * @date : 2022/2/14 21:58
 */
public class UserInfoConstant {

    public static final String DEFAULT_PWD = "123456";
    public static final BigDecimal DEFAULT_MONEY = new BigDecimal("1000000");

    public enum WorkStatus{
        NO_WORK("0", "无业"), WORKING("1", "正在就业");

        private final String code;
        private final String msg;

        WorkStatus(String code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}

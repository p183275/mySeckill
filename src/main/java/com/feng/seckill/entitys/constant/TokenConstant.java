package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/15 17:20
 */
public class TokenConstant {

    // 小时
    public static final int TOKEN_EXPIRE_TIME = 24;
    public static final String TOKEN = "_TOKEN";

    public enum DurationTime{
        ONE_DAY(1L, "一天过期"), TWO_DAY(2L, "两天过期"),
        THREE_DAY(3L, "三天过期");

        private Long code;
        private String msg;

        DurationTime(Long code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public Long getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
    }

}

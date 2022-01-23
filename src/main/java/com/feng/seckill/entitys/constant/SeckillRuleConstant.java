package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/17 15:39
 */
public class SeckillRuleConstant {

    public enum RuleStatus{
        NOT_EFFECT("0", "未生效"), EFFECT("1", "正在生效");

        private String code;
        private String msg;

        RuleStatus(String code, String msg){
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

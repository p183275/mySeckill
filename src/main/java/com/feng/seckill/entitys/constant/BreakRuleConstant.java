package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/20 21:02
 */
public class BreakRuleConstant {

    public enum BreakStatus{
        NOT_EFFECT("0", "未生效"), EFFECT("1", "生效");

        private String code;
        private String msg;

        BreakStatus(String code, String msg){
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

    public enum CreditStatus{
        NOT_EFFECT("0", "未生效"), EFFECT("1", "生效");

        private String code;
        private String msg;

        CreditStatus(String code, String msg){
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

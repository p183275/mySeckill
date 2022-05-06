package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/20 21:02
 */
public class BreakRuleConstant {

    public enum FilterType{
        OVERDUE(1L, "逾期处理"), WORK_STATUS(3L, "工作状态处理"),
        DEFAULTER(4L, "失信处理"), AGE(5L, "年龄处理");

        private final Long code;
        private final String msg;

        FilterType(Long code, String msg){
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

    public enum RuleType{
        OVERDUE(1L, "逾期记录"), CREDIT(4L, "失信记录");

        private Long ruleType;
        private String msg;

        RuleType(Long ruleType, String msg){
            this.ruleType = ruleType;
            this.msg = msg;
        }

        public Long getRuleType() {
            return ruleType;
        }

        public String getMsg() {
            return msg;
        }
    }

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

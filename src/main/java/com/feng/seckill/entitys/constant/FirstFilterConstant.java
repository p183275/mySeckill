package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/21 15:22
 */
public class FirstFilterConstant {

    public enum FilterStatus{
        NOT_PASS("0", "未通过"), PASS("1", "通过");

        private String code;
        private String msg;

        FilterStatus(String code, String msg){
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

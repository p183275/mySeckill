package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/3/28 20:22
 */
public class OrderConstant {

    public enum PayStatus{
        NOT_PAY("0", "未支付"), PAY("1", "已支付");

        private final String code;
        private final String msg;

        PayStatus(String code, String msg){
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

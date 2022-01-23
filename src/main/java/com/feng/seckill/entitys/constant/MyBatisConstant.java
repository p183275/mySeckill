package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/15 18:38
 */
public class MyBatisConstant {

    public enum LogicDelete{
        NOT_DELETE("0", "逻辑不删除"), DELETE("1", "逻辑删除");

        private final String code;
        private final String msg;

        LogicDelete(String code, String msg){
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

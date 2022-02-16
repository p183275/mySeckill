package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/2/14 21:58
 */
public class UserInfoConstant {

    public enum WorkStatus{
        NO_WORK("0", "无业/失业"), WORKING("1", "正在就业");

        private String code;
        private String msg;

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

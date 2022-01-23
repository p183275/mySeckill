package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/15 18:31
 */
public class RoleConstant {

    public enum RoleEnum{
        ADMIN_ROLE(1L, "管理员"), DEFAULT_ROLE(2L, "个人客户");

        private final Long code;
        private final String msg;

        RoleEnum(Long code, String msg){
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

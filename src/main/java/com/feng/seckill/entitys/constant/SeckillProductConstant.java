package com.feng.seckill.entitys.constant;


/**
 * @author : pcf
 * @date : 2022/1/16 16:44
 */
public class SeckillProductConstant {

    public static final String PRODUCT_ACTIVITY_URL = "http://localhost:8888/get/seckill/do/";
    public static final String PRODUCTION_SUCCESS = "抢购成功!";
    public static final String PRODUCTION_FILED = "抢购失败，库存已空!";
    public static final String URL_FILED = "抢购失败，链接错误!";
    public static final String URL_NULL = "抢购失败，链接为空!";


    public enum ProductStatus{
        NOT_START("0", "未开始"), START_ING("1", "正在进行"), FINISHED("2", "已结束");

        private String code;
        private String msg;

        ProductStatus(String code, String msg){
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

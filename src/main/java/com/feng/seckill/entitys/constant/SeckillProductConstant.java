package com.feng.seckill.entitys.constant;


/**
 * @author : pcf
 * @date : 2022/1/16 16:44
 */
public class SeckillProductConstant {

    public static final String PRODUCT_ACTIVITY_URL = "http://68.79.16.16:8888/api/get/seckill/do/";
    public static final String PRODUCTION_SUCCESS = "抢购成功！";
    public static final String PRODUCTION_FILED = "抢购失败，库存已空！";
    public static final String PERMISSION_FILED = "抱歉！您不符合购买条件！";
    public static final String URL_FILED = "抢购失败，链接错误！";
    public static final String URL_NULL = "抢购失败，链接为空！";

    // URL 默认长度
    public static final int URL_DEFAULT_LEN = 50;


    public enum ProductStatus{
        NOT_START("0", "未开始"), START_ING("1", "正在进行"), FINISHED("2", "已结束");

        private final String code;
        private final String msg;

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

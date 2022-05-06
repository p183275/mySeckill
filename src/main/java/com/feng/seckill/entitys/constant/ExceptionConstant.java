package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/16 13:42
 */
public class ExceptionConstant {

    public static final String PHONE_NUMBER_NOT_ILLEGAL_EXCEPTION = "电话号码格式错误";
    public static final String PHONE_NUMBER_NOT_NULL_EXCEPTION = "电话号码不能为空";
    public static final String CHECK_CODE_ERROR_EXCEPTION = "电话号码或验证码输入错误,或验证码过期";
    public static final String CHECK_CODE_MANY_TIME = "60秒内请勿重复发送短信！";

    public static final String LOGIN_ACCOUNT_NOT_NULL_EXCEPTION = "登录账号不能为空";
    public static final String LOGIN_ACCOUNT_OR_PASSWORD_FILED_EXCEPTION = "登录账号或密码错误，请重试";

    public static final String DATA_NOT_NULL_EXCEPTION = "数据不能为空";
    public static final String DATA_ILLEGAL_EXCEPTION = "数据格式有误";

    public static final String HIT_MANY_TIMES_EXCEPTION = "10秒中之内请勿多次点击购买！";
    public static final String PRODUCT_NOT_START_EXCEPTION = "活动未开始！";

    public static final String MONEY_IS_NOT_ENOUGH = "余额不足！";
    public static final String ACCOUNT_FILED = "银行卡号错误!";
    public static final String PAY_PASSWORD_FILED = "支付密码错误!";
    public static final String BUY_MORE_THEN_ONE = "请勿重复购买！";
}

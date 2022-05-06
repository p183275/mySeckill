package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/20 22:26
 */
public class RedisConstant {

    public static final String PRODUCTIONS = "PRODUCTIONS"; // 秒杀活动
    public static final String PRODUCTION_NUMBER = "PRODUCTION_NUMBER_"; // 产品的数量 id + ...
    public static final String PRODUCTION_URL = "PRODUCTION_URL_"; // 秒杀活动链接 id  + ...s
    public static final Integer PRODUCTIONS_EXPIRED_TIME = 25; // 产品的过期时间为 20 min
    public static final Integer PRODUCTION_URL_EXPIRED_TIME = 20; // 产品连接的过期时间为 20 min

    // 用户点击标记
    public static final String HIT = "_HIT";
    // 活动开始的set
    public static final String STARTING_PRODUCT = "SET_STARTING_PRODUCT";


    public static final String EFFECT_RULES = "EFFECT_RULES"; // 正在生效的规则
    public static final String BREAK_RULE_IDS = "BREAK_RULE_IDS"; // 正在生效的规则id
    public static final Integer EFFECT_RULES_EXPIRED_TIME = 15; // 正在生效的规则的过期时间为 15 min
    public static final Integer BREAK_RULE_IDS_EXPIRED_TIME = 10; // 正在生效的规则id的过期时间为 10 min

    // 初筛规则配置
    public static final String FILTER_OVERDUE_ENTITY = "FILTER_OVERDUE_ENTITY"; // 逾期实体配置类
    public static final String FILTER_WORK_STATUS = "FILTER_WORK_STATUS"; // 工作状态条件
    public static final String FILTER_AGE = "FILTER_AGE"; // 工作状态条件

    // 订单
    public static final String ORDER = "ORDER_";
    public static final Integer ORDER_EXPIRE_TIME = 12;

    // SET 集合的默认数值
    public static final String SET_DEFAULT_NUMBER = "0";
    // 已购买
    public static final String SET_BUY = "SET_BUY_";
    // 已下订单
    public static final String SET_ORDER = "SET_ORDER_";

    // IP 拦截
    public static final String SET_BLACK_TABLE = "SET_BLACK_TABLE";
    public static final String IP_COUNT = "IP_COUNT_";
    // 是否开启黑名单功能
    public static final String BLACK_TABLE_SWITCH = "BLACK_TABLE_SWITCH";
    // 黑名单开启
    public static final String BLACK_TABLE_START_FLAG = "1";

    // 银行账户
    public static final String BANK_ACCOUNT = "BANK_ACCOUNT";

    public static final String luo_sub_stock = "local key=KEYS[1];\n" +
            "local subNum = tonumber(ARGV[1]) ;\n" +
            "local surplusStock = tonumber(redis.call('get',key));\n" +
            "if (surplusStock<=0) then return 0\n" +
            "elseif (subNum > surplusStock) then  return 1\n" +
            "else\n" +
            "    redis.call('incrby', KEYS[1], -subNum)\n" +
            "    return 2 \n" +
            "end";
}

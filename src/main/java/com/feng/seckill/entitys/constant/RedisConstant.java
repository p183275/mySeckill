package com.feng.seckill.entitys.constant;

/**
 * @author : pcf
 * @date : 2022/1/20 22:26
 */
public class RedisConstant {

    public static final String PRODUCTIONS = "PRODUCTIONS"; // 秒杀活动
    public static final String PRODUCTION_NUMBER = "PRODUCTION_NUMBER_"; // 产品的数量 id + ...
    public static final String PRODUCTION_URL = "PRODUCTION_URL_"; // 秒杀活动链接 id  + ...s
    public static final Integer PRODUCTIONS_EXPIRED_TIME = 5; // 产品的过期时间为 5 min
    public static final Integer PRODUCTION_URL_EXPIRED_TIME = 20; // 产品连接的过期时间为 20 min


    public static final String EFFECT_RULES = "EFFECT_RULES"; // 正在生效的规则
    public static final String BREAK_RULE_IDS = "BREAK_RULE_IDS"; // 正在生效的规则id
    public static final Integer EFFECT_RULES_EXPIRED_TIME = 15; // 正在生效的规则的过期时间为 15 min
    public static final Integer BREAK_RULE_IDS_EXPIRED_TIME = 10; // 正在生效的规则id的过期时间为 10 min
}

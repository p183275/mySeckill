package com.feng.seckill.interceptor;

import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author : pcf
 * @date : 2022/4/1 23:26
 */
@Component
public class IPBlockInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 拿到黑名单开关状态
        String open = redisTemplate.opsForValue().get(RedisConstant.BLACK_TABLE_SWITCH);

        // 判断是否开启
        open = open == null ? RedisConstant.BLACK_TABLE_START_FLAG : open;

        // 开启之后运行逻辑代码
        if (RedisConstant.BLACK_TABLE_START_FLAG.equals(open)){
            boolean blackTable = blackTable(request);

            // true 代表用户可以正常访问 false 代表用户被封禁
            if (!blackTable){
                throw new RuntimeException("你的IP地址已被封禁！");
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 封禁 IP 方法
     * @param request 请求
     * @return true - 同意访问 false - 封禁IP
     */
    public boolean blackTable(HttpServletRequest request){
        // 拿到用户 ip 地址
        String ipAddr = IPUtils.getIpAddr(request);
        // 首先判断用户是否在黑名单中
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        Boolean member = opsForSet.isMember(RedisConstant.SET_BLACK_TABLE, ipAddr);
        // 如果 member 值为空则默认不在黑名单中
        member = member != null && member;


        if (member){ // 如果此 ip 在黑名单中直接拦截
            return false;
        }else {
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

            // 判断 key 是否存在
            Boolean setIfAbsent = opsForValue.setIfAbsent(RedisConstant.IP_COUNT + ipAddr, "1",
                    10, TimeUnit.SECONDS);
            setIfAbsent = setIfAbsent != null && setIfAbsent;

            // 失败则代表 key 存在
            if (!setIfAbsent){
                Long increment = opsForValue.increment(RedisConstant.IP_COUNT + ipAddr);
                increment = increment == null ? 0L : increment;
                if (increment >= 200L){
                    opsForSet.add(RedisConstant.SET_BLACK_TABLE, ipAddr);
                    return false;
                }
            }
        }
        return true;
    }
}

package com.feng.seckill.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.feng.seckill.exception.entity.TokenException;

import java.util.Calendar;
import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/1/15 16:00
 */
public class JWTUtils {

    private static final String SIGNATURE = "!pcfhaHJI#$%";

    /**
     * 生成 token header.payload.signature
     */
    public static String getToken(Map<String, String> map, Integer expireTime) {

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, expireTime);

        JWTCreator.Builder builder = JWT.create();

        // payload
        map.forEach(builder::withClaim);

        String token = builder.withExpiresAt(instance.getTime()) // 指定令牌过期的时间.
                .sign(Algorithm.HMAC256(SIGNATURE));// 签名

        return token;
    }

    /**
     * 验证token合法性
     *
     * @param token 传入token
     */
    public static DecodedJWT verify(String token) {

        try {
            // 验签对象
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGNATURE)).build();
            DecodedJWT verify = null;

            // 验证token
            verify = jwtVerifier.verify(token);
            return verify;

        } catch (JWTVerificationException e) {
//            e.printStackTrace();
            throw new TokenException();
        }
    }

}

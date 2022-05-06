package com.feng.seckill.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.result.CommonResult;
import com.feng.seckill.util.IPUtils;
import com.feng.seckill.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author : pcf
 * @date : 2022/1/21 16:03
 */
public class TokenAuthFilter extends BasicAuthenticationFilter {

    public TokenAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // 过滤规则
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 获取当前用户认证成功后的权限信息
        UsernamePasswordAuthenticationToken authRequest = null;
        try {
            authRequest = getAuthentication(request);
        }catch (Exception e){
            CommonResult<Object> result = new CommonResult<>(444, e.getMessage());

            // 将返回体转化为json字符串
            String json = new ObjectMapper().writeValueAsString(result);

            // 设置编码类型
            response.setContentType("application/json;charset=UTF-8");

            // 返回
            response.getWriter().write(json);
            return;
        }

        // 如果有权限信息放入权限上下文中
        if (authRequest != null) {
            SecurityContextHolder.getContext().setAuthentication(authRequest);
        }

        // 过滤器放行
        chain.doFilter(request, response);
    }


    /**
     * 拿到权限列表
     *
     * @param request 请求
     * @return 使用用户信息编写的token
     */
    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = null;

        // 从header中获取token
        token = request.getHeader("token");

        if (token != null) {
            // 从token中获取用户信息
            DecodedJWT verify = JWTUtils.verify(token);
            String loginAccount = verify.getClaim("loginAccount").asString();

            return new UsernamePasswordAuthenticationToken(loginAccount, token, null);
        }
        return null;
    }

}

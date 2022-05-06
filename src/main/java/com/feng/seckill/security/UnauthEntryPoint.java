package com.feng.seckill.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feng.seckill.entitys.result.CommonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : pcf
 * @date : 2022/1/15 18:19
 */
public class UnauthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {
        // 创建返回体
        CommonResult<Object> result = new CommonResult<>(444, "请登录后再访问!");

        // 将返回体转化为json字符串
        String json = new ObjectMapper().writeValueAsString(result);

        // 设置编码类型
        response.setContentType("application/json;charset=UTF-8");

        // 放入响应中
        response.getWriter().println(json);

    }

}

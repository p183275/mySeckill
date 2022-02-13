package com.feng.seckill.config;

import com.feng.seckill.filter.TokenAuthFilter;
import com.feng.seckill.filter.TokenLoginFilter;
import com.feng.seckill.security.DefaultPasswordEncoder;
import com.feng.seckill.security.TokenLogoutHandler;
import com.feng.seckill.security.UnauthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author : pcf
 * @date : 2022/1/15 18:20
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RedisTemplate redisTemplate;
    private UserDetailsService userDetailsService;
    private DefaultPasswordEncoder passwordEncoder;

    @Autowired
    public TokenWebSecurityConfig(RedisTemplate redisTemplate, UserDetailsService userDetailsService,
                                  DefaultPasswordEncoder passwordEncoder) {

        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 在其中设置自定义处理和自定义过滤器
     *
     * @param http 参数
     * @throws Exception 所有异常
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new UnauthEntryPoint()) // 无权访问
                .and().csrf().disable() // 关闭跨站请求伪造防护
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/user/logout") // 退出url
                .addLogoutHandler(new TokenLogoutHandler(redisTemplate))
                .and().addFilter(new TokenLoginFilter(authenticationManager(), redisTemplate))
                .addFilter(new TokenAuthFilter(authenticationManager(), redisTemplate))
                .httpBasic();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    // 不参与认证的路径。可以直接访问
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/user/get/check/code", "/user/register", "/user/admin/login",
                "/product/get/all/info", "/product/add/info", "/product/update/info", "/product/delete/by/id",
                "/product/update/url",
                "/rule/get/all/info", "/rule/add/info", "/rule/update/info", "/rule/delete/by/id",
                "/rule/add/activity/rules","/rule/test/permission",
                "/user/get/user/info","/user/update/info","/user/delete/by/id",
                "/break/rule/get/info", "/break/rule/add/info", "/break/rule/update/info", "/break/rule/delete/info",
                // 并发请求测试
                "/get/seckill/show/productions",
                // knif4j 所需
                "/webjars/**", "/favicon.ico", "/doc.html", "/v2/api-docs",
                // swagger-需要的静态资源
                "/swagger-ui/**",
                "/v3/api-docs",
                "/swagger-resources/**",
                "/swagger-resources"
        );
    }


}

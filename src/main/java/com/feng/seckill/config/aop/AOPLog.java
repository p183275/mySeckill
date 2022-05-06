package com.feng.seckill.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author : pcf
 * @date : 2022/3/14 16:50
 */
@Slf4j
//@Component
//@Aspect
public class AOPLog {

    // 本类引用 pointCut() 其他类全类名
    @Pointcut(value = "execution(* com.feng.seckill.controller..*.*(..))")
    public void pointCut(){};

    @Before(value = "pointCut()")
    public void logStart(JoinPoint joinPoint){
        String method = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        log.info("{}运行。。。参数列表是{}", method, Arrays.toString(arguments));
    }

    @After(value = "pointCut()")
    public void logEnd(JoinPoint joinPoint){
        String method = joinPoint.getSignature().getName();
        log.info("{}结束", method);
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void logReturn(JoinPoint joinPoint, Object result){
        String method = joinPoint.getSignature().getName();
        log.info("{}正常运行，返回值为{}", method, result);
    }

    @AfterThrowing(value = "pointCut()", throwing = "e")
    public void logError(JoinPoint joinPoint, Exception e){
        String method = joinPoint.getSignature().getName();
        log.error("{}发送错误，错误为{}", method, e.getMessage());
    }
}

package org.apache.bigtop.manager.agent.stack.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.stack.StackEnv;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Aspect
@Component
public class ScriptStartAOP {
    @Resource
    private StackEnv stackEnv;

    @Pointcut(value = "execution(* org.apache.bigtop.manager.agent.stack.service.*.*Script.start(..))")
    public void pointCut() {
    }

    /**
     * 前置通知，在切点执行之前执行的操作
     */
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        // 逻辑代码
        log.info("before start point");
    }

    /**
     * 后置通知，在切点执行之前执行的操作
     */
    @After("pointCut()")
    public void after(JoinPoint joinPoint) {
        // 逻辑代码
        log.info("after start point");
    }

}

package com.arbitrage.scheduler.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import static com.arbitrage.api.constants.CommonConstants.LOG_ID_KEY;
import static com.arbitrage.common.utils.LogUtils.generateRandomString;

@Aspect
@Component
public class SchedulerLoggingAspect {

    @Pointcut("execution(* com.arbitrage.scheduler.timer.*(..))")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            String logId = MDC.get(LOG_ID_KEY);
            if (logId == null) {
                logId = generateRandomString(16);
                MDC.put(LOG_ID_KEY, logId);
            }
            return joinPoint.proceed();
        } finally {
            MDC.remove(LOG_ID_KEY);
        }
    }

}

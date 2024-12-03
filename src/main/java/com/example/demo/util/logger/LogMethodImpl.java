package com.example.demo.util.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class LogMethodImpl {

    private final Map<String, Long> startTimeMap = new ConcurrentHashMap<>();

    @Before("@annotation(LogMethod)")
    public void logBefore(JoinPoint joinPoint) {
        String key = generateKey(joinPoint);
        startTimeMap.put(key, System.currentTimeMillis());
        log("Starting", joinPoint);
    }

    @After("@annotation(LogMethod)")
    public void logAfter(JoinPoint joinPoint) {
        String key = generateKey(joinPoint);
        Long startTime = startTimeMap.remove(key);
        long endTime = System.currentTimeMillis();

        log("Finished", joinPoint);
        if (startTime != null) {
            long duration = endTime - startTime;
            logDuration(duration, joinPoint);
        } else {
            getLogger(joinPoint).warn("[Thread ID:{}] Unable to calculate duration for method {} in {}.",
                    Thread.currentThread().getId(),
                    joinPoint.getSignature().getName(),
                    joinPoint.getTarget().getClass().getSimpleName());
        }
    }

    private void log(String status, JoinPoint joinPoint) {
        Logger logger = getLogger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("[Thread ID:{}] {} {} in {}.",
                Thread.currentThread().getId(), status, methodName, className);
    }

    private void logDuration(long duration, JoinPoint joinPoint) {
        Logger logger = getLogger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("[Thread ID:{}] Method {} in {} took {} ms.",
                Thread.currentThread().getId(), methodName, className, duration);
    }

    private Logger getLogger(JoinPoint joinPoint) {
        return LogManager.getLogger(joinPoint.getTarget().getClass());
    }

    private String generateKey(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName() + "#" + joinPoint.getSignature().getName();
    }
}

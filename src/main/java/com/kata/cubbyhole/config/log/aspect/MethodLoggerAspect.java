package com.kata.cubbyhole.config.log.aspect;

import com.kata.cubbyhole.config.log.LogWriter;
import com.kata.cubbyhole.config.log.annotation.MethodLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class MethodLoggerAspect {

    @Pointcut("@within(com.kata.cubbyhole.config.log.annotation.MethodLogger)" +
            "|| @annotation(com.kata.cubbyhole.config.log.annotation.MethodLogger)")
    public void methodLoggerPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around(value = "methodLoggerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        MethodLogger loggableMethod = method.getAnnotation(MethodLogger.class);
        MethodLogger loggableClass = point.getTarget().getClass().getAnnotation(MethodLogger.class);

        LogLevel logLevel = loggableMethod != null ? loggableMethod.value() : loggableClass.value();

        String star = "**********";
        LogWriter.write(point.getTarget().getClass(), logLevel, star + method.getName() + "() start execution" + star);

        if (point.getArgs() != null && point.getArgs().length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < point.getArgs().length; i++) {
                sb.append(method.getParameterTypes()[i].getName() + ":" + point.getArgs()[i]);
                if (i < point.getArgs().length - 1)
                    sb.append(", ");
            }

            LogWriter.write(point.getTarget().getClass(), logLevel, method.getName() + "() args Type: " + sb.getClass().getName() + " args: " + sb);
        }

        long startTime = System.currentTimeMillis();
        Object result = point.proceed();

        long endTime = System.currentTimeMillis();

        if (result != null) {
            LogWriter.write(point.getTarget().getClass(), logLevel, method.getName() + "() Result Type: " + result.getClass().getName() + " Result: " + result);
        }

        LogWriter.write(point.getTarget().getClass(), logLevel, star + method.getName() + "() finished execution and takes " + (endTime - startTime) + " millis time to execute " + star);

        return result;
    }
}

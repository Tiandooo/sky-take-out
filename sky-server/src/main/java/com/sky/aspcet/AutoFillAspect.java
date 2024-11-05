package com.sky.aspcet;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * 自定义切面，实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void aotoFillPointCut(){}

    @Before("aotoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充...");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象

        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获得方法上的注解对象
        OperationType operationType = autoFill.value(); // 获得数据库操作类型

        Object[] args = joinPoint.getArgs();

        if (args.length == 0 || args == null){
            return;
        }

        Object entity = args[0];
        Long currrentId = BaseContext.getCurrentId();
        LocalDateTime currentTime = LocalDateTime.now();

        if (operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                setCreateTime.invoke(entity, currentTime);
                setCreateUser.invoke(entity, currrentId);
                setUpdateTime.invoke(entity, currentTime);
                setUpdateUser.invoke(entity, currrentId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                setUpdateTime.invoke(entity, currentTime);
                setUpdateUser.invoke(entity, currrentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}

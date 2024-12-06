package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
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
 * 自定义切面 实现公共字段自动填充
 */
@Aspect // 切面
@Component // 注入到Spring容器
@Slf4j // 日志
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)") // 切点表达式 拦截mapper包下 被AutoFill注解的方法
    public void autoFillPointcut() { // 切点方法
    }

    @Before("autoFillPointcut()") // 前置通知
    public void before(JoinPoint joinPoint) { // 前置通知方法
        log.info("自动填充切面执行");
        // 获取当前拦截到的方法上的注解标识的数据库操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature(); // 获取方法签名对象
        OperationType operationType = methodSignature.getMethod().getAnnotation(AutoFill.class).value(); // 获取方法上的AutoFill注解的value值
        // 获取当前拦截方法的参数 也就是实体对象
        Object[] args = joinPoint.getArgs(); // 获取方法参数
        if (args == null || args.length == 0) { // 判断参数是否为空
            return;
        }
        // 获取实体对象
        Object entity = args[0];

        // 为实体对象的公共字段赋值
        // 1、获取当前登录用户的id
        Long currentUserId = BaseContext.getCurrentId();
        // 2、判断是新增还是修改
        if (operationType == OperationType.INSERT) { // 新增
            // 为实体对象的创建时间、更新时间赋值 为实体对象的创建人、更新人赋值
            try {
                Method setCreateTime= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class); // 获取setCreateTime方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class); // 获取setUpdateTime方法
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class); // 获取setCreateUser方法
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class); // 获取setUpdateUser方法

                setCreateTime.invoke(entity, LocalDateTime.now()); // 调用setCreateTime方法
                setUpdateTime.invoke(entity, LocalDateTime.now()); // 调用setUpdateTime方法
                setCreateUser.invoke(entity, currentUserId); // 调用setCreateUser方法
                setUpdateUser.invoke(entity, currentUserId); // 调用setUpdateUser方法

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            //
        } else if (operationType == OperationType.UPDATE) { // 修改
            // 为实体对象的更新人赋值
            // 为实体对象的更新时间赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class); // 获取setUpdateTime方法
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class); // 获取setUpdateUser方法

                setUpdateTime.invoke(entity, LocalDateTime.now()); // 调用setUpdateTime方法
                setUpdateUser.invoke(entity, currentUserId); // 调用setUpdateUser方法
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }



    }

}

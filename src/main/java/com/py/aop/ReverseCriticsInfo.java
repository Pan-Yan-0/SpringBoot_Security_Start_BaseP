package com.py.aop;

import com.py.domain.OperateLog;
import com.py.mapper.OperateLogMapper;
import com.py.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
 * @Author PY
 * 本处使用AOP方法进行用户对重要数据操作时
 * 保存进数据库中，方便以后回档操作或者其他操作
 * @TODO ALL OF AOPCLASS
 */
@Slf4j
@Aspect
@Component
public class ReverseCriticsInfo {
    @Autowired
    private OperateLogMapper operateLogMapper;
    @Autowired
    private HttpServletRequest request;
    @Pointcut("execution(* com.py.service.UserService.updateEmail(..))")
    private void pt(){}
    @Pointcut("execution(* com.py.service.UserService.updatePassword(..))")
    private void pt1(){}
    @Transactional
    @Around("pt()||pt1()")
    public Object recordImportant(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取起始时间
        long begin = System.currentTimeMillis();
        // 从token中获取id值
        String token = request.getHeader("token");
        //解析token
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        Long user_id;
        try {
            user_id = Long.parseLong(userid);

        } catch (NumberFormatException e) {
            throw e;
        }
        log.info("正在修改重要参数中...");

        //1. 获取 目标对象的类名 .
        String className = joinPoint.getTarget().getClass().getName();
        log.info("目标对象的类名:{}", className);

        //2. 获取 目标方法的方法名 .
        String methodName = joinPoint.getSignature().getName();
        log.info("目标方法的方法名: {}",methodName);

        //3. 获取 目标方法运行时传入的参数 .
        Object[] args = joinPoint.getArgs();
        log.info("目标方法运行时传入的参数: {}", Arrays.toString(args));

        //4. 放行 目标方法执行 .
        Object result = joinPoint.proceed();
        //获取运行结束时间
        long end = System.currentTimeMillis();
        //创建日记类

        OperateLog operateLog = new OperateLog();
        operateLog.setOperate_user(user_id);
        operateLog.setOperate_time(new Date());
        operateLog.setClass_name(className);
        operateLog.setMethod_name(methodName);
        operateLog.setMethod_params(Arrays.toString(args));
        operateLog.setReturn_value(result.toString());
        operateLog.setCost_time(end- begin);
        operateLogMapper.add(operateLog);

        //5. 获取 目标方法运行的返回值 .
        log.info("目标方法运行的返回值: {}",result);

        log.info("MyAspect8 around after ...");
        return result;
    }

}

package cn.weizuxiao.framework.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import cn.weizuxiao.framework.context.SpringContext;
import lombok.SneakyThrows;
/**
 * redis锁实现
 */
@Aspect
@ConditionalOnClass(name = "org.redisson.api.RedissonClient")
public class RedisLockAspectHandler {

    private static RedissonClient redissonClient;

    /**
     * around切面
     * 
     * @param joinPoint
     * @return
     */
    @SneakyThrows
    @Around("@annotation(cn.weizuxiao.framework.redis.RedisLock)")
    public Object around(ProceedingJoinPoint joinPoint) {
        synchronized (this) {
            if (!Optional.ofNullable(redissonClient).isPresent()) {
                redissonClient = SpringContext.getBean(RedissonClient.class);
            }
        }
        String key = parseKey(joinPoint);
        RLock rLock = redissonClient.getLock(key);
        rLock.lock();
        try {
            return joinPoint.proceed();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 获取key, el表达式格式
     * 
     * @param el
     * @param joinPoint
     * @return
     */
    private String parseKey(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RedisLock lock = method.getAnnotation(RedisLock.class);
        if (!Optional.ofNullable(lock).isPresent()) {
            return "";
        }
        String el = lock.value();
        ParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();
        String[] names = discover.getParameterNames(method);
        if (null == names || ObjectUtils.isEmpty(names)) {
            return el;
        }
        Object[] args = joinPoint.getArgs();
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(el);
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < names.length; i++) {
            context.setVariable(names[i], args[i]);
        }
        return expression.getValue(context, String.class);
    }
}

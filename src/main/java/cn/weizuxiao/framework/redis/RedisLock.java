package cn.weizuxiao.framework.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis锁
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 需添加此声明，否则运行时无法获取注解信息
@Target({ElementType.METHOD})
public @interface RedisLock {

    /**
     * el表达式
     * @return
     */
    String value() default "";
}

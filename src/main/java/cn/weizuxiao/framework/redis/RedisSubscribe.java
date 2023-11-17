package cn.weizuxiao.framework.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * redis订阅
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 需添加此声明，否则运行时无法获取注解信息
@Target({ ElementType.TYPE })
@Component
public @interface RedisSubscribe {

    String[] value() default {};

}

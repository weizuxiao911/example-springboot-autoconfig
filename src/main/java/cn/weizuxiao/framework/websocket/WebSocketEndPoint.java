package cn.weizuxiao.framework.websocket;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * websocket端点
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 需添加此声明，否则运行时无法获取注解信息
@Target({ElementType.TYPE})
@Component
public @interface WebSocketEndPoint {

    String path() default "/";

    String origin() default "*";

    Class<? extends HandshakeInterceptor>[] usingHandshakeInterceptor() default {};

    boolean supportsPartialMessages() default false;

}

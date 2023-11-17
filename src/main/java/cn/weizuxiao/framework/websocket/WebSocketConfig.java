package cn.weizuxiao.framework.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cn.weizuxiao.framework.context.SpringContext;
import lombok.extern.slf4j.Slf4j;

/**
 * websocket配置
 */
@Slf4j
@ConditionalOnClass(name = "org.springframework.web.socket.config.annotation.WebSocketConfigurer")
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private ConcurrentHashMap<String, DefaultWebSocketHandler> handler = new ConcurrentHashMap<>(16);

    public WebSocketConfig(final Map<String, DefaultWebSocketHandler> map) {
        map.entrySet().forEach(kv -> {
            handler.put(kv.getKey(), kv.getValue());
        });
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        if (!handler.isEmpty()) {
            handler.values().forEach(it -> {
                if (!it.getClass().isAnnotationPresent(WebSocketEndPoint.class)) {
                    return;
                }
                WebSocketEndPoint endPoint = it.getClass().getAnnotation(WebSocketEndPoint.class);
                List<HandshakeInterceptor> list = Stream.of(endPoint.usingHandshakeInterceptor())
                        .map(i -> SpringContext.getBean(i))
                        .collect(Collectors.toList());
                log.info("path = {} origin = {}", endPoint.path(), endPoint.origin());
                registry.addHandler(it, endPoint.path())
                        .addInterceptors(list.toArray(new HandshakeInterceptor[list.size()]))
                        .setAllowedOriginPatterns(endPoint.origin());
            });
        }
    }

}

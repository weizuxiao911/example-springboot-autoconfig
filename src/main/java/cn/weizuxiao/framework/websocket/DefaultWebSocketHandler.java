package cn.weizuxiao.framework.websocket;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * websocket处理器
 */
public interface DefaultWebSocketHandler extends WebSocketHandler {

    public static final ConcurrentHashMap<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>(16);

    /**
     * 完成连接后
     */
    default void afterConnectionEstablished(WebSocketSession session) {
        SESSIONS.put(session.getId(), session);
        try {
            Optional<Method> opt = Stream.of(getClass().getMethods())
                    .filter(it -> it.isAnnotationPresent(OnOpen.class))
                    .findFirst();
            if (!opt.isPresent()) {
                return;
            }
            opt.get().invoke(this, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理消息
     */
    default void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            Optional<Method> opt = Stream.of(getClass().getMethods())
                    .filter(it -> it.isAnnotationPresent(OnMessage.class))
                    .findFirst();
            if (!opt.isPresent()) {
                return;
            }
            opt.get().invoke(this, session, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理传输异常
     */
    default void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            Optional<Method> opt = Stream.of(getClass().getMethods())
                    .filter(it -> it.isAnnotationPresent(OnError.class))
                    .findFirst();
            if (!opt.isPresent()) {
                return;
            }
            opt.get().invoke(this, session, exception);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接后
     */
    default void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        SESSIONS.remove(session.getId());
        try {
            Optional<Method> opt = Stream.of(getClass().getMethods())
                    .filter(it -> it.isAnnotationPresent(OnClose.class))
                    .findFirst();
            if (!opt.isPresent()) {
                return;
            }
            opt.get().invoke(this, session, closeStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    default boolean supportsPartialMessages() {
        if (!getClass().isAnnotationPresent(WebSocketEndPoint.class)) {
            return false;
        }
        return getClass().getAnnotation(WebSocketEndPoint.class).supportsPartialMessages();
    }

    /**
     * 向指定用户发送消息
     * @param attribute
     * @param regex
     * @param message
     */
    default void send(String attribute, String regex, WebSocketMessage<?> message) {
        List<WebSocketSession> list = SESSIONS.values()
                .stream()
                .filter(it -> {
                    if (!it.getAttributes().containsKey(attribute)) {
                        return false;
                    }
                    return Pattern.compile(regex).matcher(it.getAttributes().get(attribute).toString()).find();
                })
                .collect(Collectors.toList());
        list.forEach(it -> {
            try {
                it.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 向所有用户发送消息
     * @param message
     */
    default void send(WebSocketMessage<?> message) {
        SESSIONS.values().forEach(it -> {
            try {
                it.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}

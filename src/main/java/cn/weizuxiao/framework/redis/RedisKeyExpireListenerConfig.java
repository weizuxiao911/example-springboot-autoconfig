package cn.weizuxiao.framework.redis;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * redis key过期消息监听
 */
@Slf4j
@SuppressWarnings("all")
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@RedisSubscribe("__key*__:expired")
public class RedisKeyExpireListenerConfig extends RedisMessageListener<String> {

    private ConcurrentHashMap<String, RedisKeyExpireListener> listener = new ConcurrentHashMap<>(16);

    public RedisKeyExpireListenerConfig(final Map<String, RedisKeyExpireListener> map) {
        map.entrySet().forEach(kv -> {
            listener.put(kv.getKey(), kv.getValue());
        });
    }

    @Override
    public void handle(String topic, String message) {
        if (!listener.isEmpty()) {
            List<RedisKeyExpireListener> list = listener.values().stream().filter(it -> {
                if (!it.getClass().isAnnotationPresent(RedisSubscribe.class)) {
                    return false;
                }
                RedisSubscribe subscribe = it.getClass().getAnnotation(RedisSubscribe.class);
                String[] keys = subscribe.value();
                if (null == keys || keys.length == 0) {
                    return false;
                }
                return Stream.of(keys).anyMatch(s -> "*".equals(s) || Pattern.compile(s).matcher(message).find());
            }).collect(Collectors.toList());
            list.parallelStream().forEach(it -> it.handle(message));
        }
    }

}

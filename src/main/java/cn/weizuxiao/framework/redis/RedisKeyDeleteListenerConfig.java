package cn.weizuxiao.framework.redis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import lombok.extern.slf4j.Slf4j;

/**
 * redis key 删除事件
 * 
 * @author weizuxiao
 */
@Slf4j
@SuppressWarnings("all")
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@RedisSubscribe("__key*__:del")
public class RedisKeyDeleteListenerConfig extends RedisMessageListener<String> {

    private ConcurrentHashMap<String, RedisKeyDeleteListener> listener = new ConcurrentHashMap<>(16);

    public RedisKeyDeleteListenerConfig(final Map<String, RedisKeyDeleteListener> map) {
        map.entrySet().forEach(kv -> {
            listener.put(kv.getKey(), kv.getValue());
        });
    }

    @Override
    public void handle(String topic, String message) {
        if (!listener.isEmpty()) {
            List<RedisKeyDeleteListener> list = listener.values().stream().filter(it -> {
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

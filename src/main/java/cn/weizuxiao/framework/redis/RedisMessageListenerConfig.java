package cn.weizuxiao.framework.redis;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * redis消息监听配置
 * 
 * @author weizuxiao
 */
@Slf4j
@SuppressWarnings("all")
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
public class RedisMessageListenerConfig {

    private ConcurrentHashMap<String, RedisMessageListener> listener = new ConcurrentHashMap<>(16);

    public RedisMessageListenerConfig(final Map<String, RedisMessageListener> map) {
        map.entrySet().forEach(kv -> {
            listener.put(kv.getKey(), kv.getValue());
        });
    }

    @Bean
    @SuppressWarnings("unchecked")
    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        if (!listener.isEmpty()) {
            listener.values().forEach(it -> {
                if (Optional.ofNullable(it.topics()).isPresent() && !it.topics().isEmpty()) {
                    log.info("listen = {}, topics = {}", it, it.topics());
                    container.addMessageListener(it, it.topics());
                }
            });
        }
        return container;
    }

}

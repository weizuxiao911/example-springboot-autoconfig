package cn.weizuxiao.framework.redis;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import org.springframework.lang.Nullable;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.connection.Message;

/**
 * 拓展redis消息监听
 */
@Slf4j
public abstract class RedisMessageListener<T> implements MessageListener {

    @Resource
    private RedisTemplate<String, T> redisTemplate;

    public abstract void handle(String topic, T message);

    public List<PatternTopic> topics() {
        if (!getClass().isAnnotationPresent(RedisSubscribe.class)) {
            return null;
        }
        RedisSubscribe subscribe = getClass().getAnnotation(RedisSubscribe.class);
        String[] topics = subscribe.value();
        if (null == topics || topics.length == 0) {
            return null;
        }
        return Stream.of(topics).map(it -> new PatternTopic(it)).collect(Collectors.toList());
    }

    @SuppressWarnings("all")
    public void onMessage(Message message, @Nullable byte[] pattern) {
        log.info("pattern = {}, message = {}", new String(pattern), message);
        try {
            T obj = (T) redisTemplate.getValueSerializer().deserialize(message.getBody());
            this.handle(new String(pattern), obj);
            return;
        } catch (Exception e) {
            this.handle(new String(pattern), (T) new String(message.getBody()));
        }
    }

}

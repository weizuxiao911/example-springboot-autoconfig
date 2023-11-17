package cn.weizuxiao.framework.redis;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * redis缓存管理配置
 */
@Slf4j
@ConditionalOnClass(name = "org.springframework.data.redis.cache.RedisCacheManager")
@EnableCaching
public class RedisCacheManagerConfig {

        @Value("${spring.redis.entryTtl: 30}")
        private Long entryTtl;

        @Bean
        @SuppressWarnings("all")
        public CacheManager cacheManager(RedisConnectionFactory factory) {
                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(entryTtl))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

                RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                                .cacheDefaults(config)
                                .build();
                log.info("redis cache manager load completed...");
                return cacheManager;
        }

        @SuppressWarnings("all")
        private Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
                Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
                ObjectMapper om = new ObjectMapper();
                // 设置ObjectMapper访问权限
                om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                // 记录序列化之后的数据类型，方便反序列化
                om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                                ObjectMapper.DefaultTyping.NON_FINAL);

                // LocalDatetime序列化，默认不兼容jdk8日期序列化
                JavaTimeModule timeModule = new JavaTimeModule();
                timeModule.addDeserializer(LocalDate.class,
                                new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                timeModule.addDeserializer(LocalDateTime.class,
                                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                timeModule.addSerializer(LocalDate.class,
                                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                timeModule.addSerializer(LocalDateTime.class,
                                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                // 关闭默认的日期格式化方式，默认UTC日期格式 yyyy-MM-dd’T’HH:mm:ss.SSS
                om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                om.registerModule(timeModule);

                jackson2JsonRedisSerializer.setObjectMapper(om);
                return jackson2JsonRedisSerializer;
        }

}

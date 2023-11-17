package cn.weizuxiao.framework.redis;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * redisson配置
 */
@ConditionalOnClass(name = "org.redisson.api.RedissonClient")
public class RedissonConfig {

    @Resource
    private RedissonProperties properites;

    @Bean
    public RedissonClient create() {
        if (StringUtils.isEmpty(properites.getHost())) {
            return null;
        }
        String addr = new StringBuilder("redis://").append(properites.getHost()).append(":").append(properites.getPort()).toString();
        Config config = new Config();
        config.useSingleServer()
                .setAddress(addr)
                .setDatabase(properites.getDb())
                .setConnectionMinimumIdleSize(properites.getMinimumIdleSize())
                .setConnectionPoolSize(properites.getConnectionPoolSize())
                .setRetryAttempts(properites.getRetryAttempts())
                .setRetryInterval(properites.getRetryInterval())
                .setPingConnectionInterval(properites.getPingConnectionInterval())
                .setTimeout(properites.getTimeout());
        if (!StringUtils.isEmpty(properites.getPassword())) {
            config.useSingleServer().setPassword(properites.getPassword());
        }
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }


}

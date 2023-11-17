package cn.weizuxiao.framework.redis;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

/**
 * redisson 属性
 */
@Data
public class RedissonProperties {

    @Value("${redisson.host:}")
    private String host;

    @Value("${redisson.password:}")
    private String password = "";

    @Value("${redisson.port:6379}")
    private Integer port;

    @Value("${redisson.db:0}")
    private Integer db;

    @Value("${redisson.minimumIdleSize:5}")
    private Integer minimumIdleSize;

    @Value("${redisson.connectionPoolSize:100}")
    private Integer connectionPoolSize;

    @Value("${redisson.retryAttempts:3}")
    private Integer retryAttempts;

    @Value("${redisson.retryInterval:30000}")
    private Integer retryInterval;

    @Value("${redisson.pingConnectionInterval:30000}")
    private Integer pingConnectionInterval;

    @Value("${redisson.timeout:30000}")
    private Integer timeout;

}


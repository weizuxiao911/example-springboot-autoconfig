package cn.weizuxiao.framework.redis;

/**
 * redis key过期监听
 */
public interface RedisKeyExpireListener {

    /**
     * 过期键
     * 
     * @param key
     */
    void handle(String key);

}

package cn.weizuxiao.framework.redis;

/**
 * redis key 删除事件监听
 */
public interface RedisKeyDeleteListener {

    void handle(String key);

}

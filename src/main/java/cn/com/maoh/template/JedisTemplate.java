package cn.com.maoh.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import java.util.List;
import java.util.Map;

/**
 * Created by maoh on 2017/12/04.
 */
public class JedisTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisTemplate.class);

    private Pool<Jedis> pool;
    private JedisCluster cluster;

    public JedisTemplate(Pool<Jedis> pool) {
        this.pool = pool;
        this.cluster = null;
    }

    public JedisTemplate(JedisCluster cluster) {
        this.cluster = cluster;
        this.pool = null;
    }

    public JedisTemplate(JedisFactory jedisFactory){
        if( jedisFactory.getRedisNodes().split(JedisFactory.HOST_SPLITTER).length > 1){
            //集群模式
            this.cluster = jedisFactory.createJedisCluster();
            this.pool = null;
        } else {
            //单机模式
            this.cluster = null;
            this.pool = jedisFactory.createJedisPool();
        }
    }

    /**
     * 左侧push
     * @param key
     * @param value
     * @return
     */
    public Long lpush(String key,String value){
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.lpush(key,value);
        }finally {
            returnConnection(jedis);
        }
    }

    /**
     * 右侧弹出
     * @param key
     * @return
     */
    public String rpop(String key){
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.rpop(key);
        }finally {
            returnConnection(jedis);
        }
    }

    /**
     * 发布消息
     * @param channel
     * @param message
     */
    public void publish(String channel, String message){
        if (isClusterMode()){
            cluster.publish(channel,message);
        }else{
            pool.getResource().publish(channel,message);
        }
    }

    /**
     * 订阅频道
     * @param jedisPubSub
     * @param channel
     */
    public void subscribe(JedisPubSub jedisPubSub, String channel){
        if (isClusterMode()){
            cluster.subscribe(jedisPubSub,channel);
        }else{
            pool.getResource().subscribe(jedisPubSub,channel);
        }
    }

    public String get(String key) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.get(key);
        } finally {
            returnConnection(jedis);
        }
    }

    public String set(String key, String value) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.set(key, value);
        } finally {
            returnConnection(jedis);
        }
    }

    /**
     * @param key
     * @param value
     * @param nxxx  NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key if it already exist.<p>
     * @param expx  EX|PX, expire time units: EX = seconds; PX = milliseconds <p>
     * @param time  expire time in the units of
     * @return
     */
    public String set(String key, String value, String nxxx, String expx, Long time) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.set(key, value, nxxx, expx, time);
        } finally {
            returnConnection(jedis);
        }
    }

    public Long del(String key) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.del(key);
        } finally {
            returnConnection(jedis);
        }
    }

    public String hmset(String key, Map<String, String> values) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.hmset(key, values);

        } finally {
            returnConnection(jedis);
        }
    }

    public Long hset(String key, String skey, String svalue) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.hset(key, skey, svalue);
        } finally {
            returnConnection(jedis);
        }
    }

    public Map<String, String> hgetAll(String key) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.hgetAll(key);
        } finally {
            returnConnection(jedis);
        }
    }

    public List<String> hmget(String key, String... k) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.hmget(key, k);
        } finally {
            returnConnection(jedis);
        }
    }

    /**
     * @param key     rediskey
     * @param seconds 秒
     * @return 剩余过期时间（秒）
     */
    public Long expire(String key, Integer seconds) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.expire(key, seconds);
        } finally {
            returnConnection(jedis);
        }
    }

    public Long ttl(String key) {
        JedisCommands jedis = borrowConnection();
        try {
            return jedis.ttl(key);
        } finally {
            returnConnection(jedis);
        }
    }

    private JedisCommands borrowConnection() {
        return isClusterMode() ? this.cluster : this.pool.getResource();
    }

    private void returnConnection(JedisCommands commands) {
        if (isPoolMode() && commands instanceof Jedis) {
            ((Jedis) commands).close();
        }
    }


    private boolean isClusterMode() {
        return this.cluster != null && this.pool == null;
    }

    private boolean isPoolMode() {
        return this.pool != null && this.cluster == null;
    }

    public void destroy() {
        if (isPoolMode()) {
            try {
                this.pool.destroy();
            } catch (Exception ex) {
                LOGGER.warn("Cannot properly close Jedis pool", ex);
            }
            pool = null;
        }
        if (isClusterMode()) {
            try {
                this.cluster.close();
            } catch (Exception ex) {
                LOGGER.warn("Cannot properly close Jedis cluster", ex);
            }
        }
    }


}

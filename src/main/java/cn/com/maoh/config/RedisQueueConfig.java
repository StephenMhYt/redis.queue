package cn.com.maoh.config;

import cn.com.maoh.handler.IMessageHandler;
import cn.com.maoh.handler.impl.DefaultMessageHandler;
import cn.com.maoh.publish.RedisPublisher;
import cn.com.maoh.subscribe.RedisSubscriber;
import cn.com.maoh.template.JedisFactory;
import cn.com.maoh.template.JedisTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/13.
 */
@Configuration
public class RedisQueueConfig {

    @Value("${redis.nodes}")
    private String redisNodes;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.pool.size}")
    private int maxTotal;

    @Value("${redis.queue.channel")
    private String channel;

    @Value("${redis.keep.alive.time}")
    private int keepAliveTime;

    @Value("${redis.max.pool.size}")
    private int maximumPoolSize;

    @Bean(name = "jedisFactory")
    public JedisFactory jedisFactory(){
        JedisFactory jedisFactory = new JedisFactory();
        jedisFactory.setRedisNodes(redisNodes);
        jedisFactory.setPassword(password);
        jedisFactory.setMaxTotal(maxTotal);
        return jedisFactory;
    }

    @Bean
    public JedisTemplate jedisTemplate(@Qualifier("jedisFactory")JedisFactory jedisFactory){
        return new JedisTemplate(jedisFactory);
    }

    @Bean
    public RedisPublisher redisPublisher(@Qualifier("jedisTemplate")JedisTemplate jedisTemplate){
        RedisPublisher redisPublisher = new RedisPublisher();
        redisPublisher.setJedisTemplate(jedisTemplate);
        return redisPublisher;
    }

    @Bean
    public DefaultMessageHandler defaultMessageHandler(){
        return new DefaultMessageHandler();
    }

    @Bean
    public RedisSubscriber redisSubscriber(@Qualifier("jedisTemplate")JedisTemplate jedisTemplate, @Qualifier("defaultMessageHandler")DefaultMessageHandler messageHandler){
        Map<String,IMessageHandler> listeners = new HashMap<>();
        listeners.put("default",defaultMessageHandler());

        RedisSubscriber redisSubscriber = new RedisSubscriber();
        redisSubscriber.setJedisTemplate(jedisTemplate);
        redisSubscriber.setChannel(channel);
        redisSubscriber.setCorePoolSize(maxTotal);
        redisSubscriber.setKeepAliveTime(keepAliveTime);
        redisSubscriber.setMaximumPoolSize(maximumPoolSize);
        redisSubscriber.setListeners(listeners);
        return redisSubscriber;
    }
}

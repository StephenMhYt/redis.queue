package cn.com.maoh.subscribe;

import cn.com.maoh.template.JedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;


/**
 * 由于redis的订阅方法subscribe是线程阻塞的，故另启一个线程订阅消息
 * Created by maoh on 2017/12/3.
 */
public class RedisSubscribeThread extends Thread{

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscribeThread.class);

    private JedisPubSub subsriber;

    private String channel;

    private JedisTemplate jedisTemplate;

    public RedisSubscribeThread(JedisPubSub subsriber,String channel,JedisTemplate jedisTemplate){
        this.subsriber = subsriber;
        this.channel = channel;
        this.jedisTemplate = jedisTemplate;
    }

    @Override
    public void run(){
        //订阅channel频道
        jedisTemplate.subscribe(subsriber, channel);
        LOGGER.info("succeed to subscribe the channel:{}",channel);
    }
}

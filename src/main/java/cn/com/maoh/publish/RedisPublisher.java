package cn.com.maoh.publish;

import cn.com.maoh.template.JedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by maoh on 2017/12/4.
 */
public class RedisPublisher{

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPublisher.class);

    private JedisTemplate jedisTemplate;

    /**
     *
     * @param channel
     * @param key
     * @param message
     */
    public void publishAndSendMessage(String channel,String key,String message){
        jedisTemplate.lpush(key,message);
        jedisTemplate.publish(channel,key);
        LOGGER.info("succeed to publish to the channel:{},and send message:{} to the key:{}"
                ,channel,message,key);
    }

    public void setJedisTemplate(JedisTemplate jedisTemplate) {
        this.jedisTemplate = jedisTemplate;
    }
}

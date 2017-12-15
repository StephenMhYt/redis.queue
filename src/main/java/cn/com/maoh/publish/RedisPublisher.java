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
     * @param queueName
     * @param message
     */
    public void publishAndSendMessage(String channel,String queueName,String message){
        jedisTemplate.lpush(queueName,message);
        jedisTemplate.publish(channel,queueName);
        LOGGER.info("succeed to publish to the channel:{},and send message:{} to the queueName:{}"
                ,channel,message,queueName);
    }

    public void setJedisTemplate(JedisTemplate jedisTemplate) {
        this.jedisTemplate = jedisTemplate;
    }
}

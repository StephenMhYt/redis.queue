package cn.com.maoh.subscribe;

import cn.com.maoh.handler.IMessageHandler;
import cn.com.maoh.template.JedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 由于在分布式项目中，多台订阅者都会收到发布者发布的消息，故利用redis的list数据结构，只会有一台rpop到相应的具体消息。
 * 使用时需在spring配置文件中配置订阅者bean，并填充相应属性
 *
 * Created by maoh on 2017/12/4.
 */
public class RedisSubscriber extends JedisPubSub implements InitializingBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscriber.class);

    private Map<String,IMessageHandler>  listeners;

    private int corePoolSize=3;

    private int maximumPoolSize=5;

    private long keepAliveTime=100L;

    private String channel;

    private ThreadPoolExecutor pool;

    private JedisTemplate jedisTemplate;

    @Override
    public final void onMessage(String channel, String message) {
        //订阅者接收到发布消息
        IMessageHandler messageHandler = listeners.get(message);
        if (messageHandler == null){
            return;
        }
        String value = jedisTemplate.rpop(message);
        if(StringUtils.isEmpty(value)){
            return;
        }

        pool.execute(new SubscribeTask(messageHandler, value));
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.info("subscribe redis channel success, channel:{}, subscribedChannels:{}", channel, subscribedChannels);
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOGGER.info("unsubscribe redis channel, channel:{}, subscribedChannels:{}", channel, subscribedChannels);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //创建线程池对象
        pool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        //由于redis的订阅方法subscribe是线程阻塞的，故另启一个线程订阅消息
        RedisSubscribeThread thread = new RedisSubscribeThread(this,channel,jedisTemplate);
        thread.start();
    }

    public Map<String, IMessageHandler> getListeners() {
        return listeners;
    }

    public void setListeners(Map<String, IMessageHandler> listeners) {
        this.listeners = listeners;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public ThreadPoolExecutor getPool() {
        return pool;
    }

    public void setPool(ThreadPoolExecutor pool) {
        this.pool = pool;
    }

    public JedisTemplate getJedisTemplate() {
        return jedisTemplate;
    }

    public void setJedisTemplate(JedisTemplate jedisTemplate) {
        this.jedisTemplate = jedisTemplate;
    }
}
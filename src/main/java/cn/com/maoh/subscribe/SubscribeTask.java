package cn.com.maoh.subscribe;

import cn.com.maoh.handler.IMessageHandler;

/**
 * Created by maoh on 2017/12/4.
 */
public class SubscribeTask implements Runnable {

    private IMessageHandler handler;

    private String message;

    public SubscribeTask(IMessageHandler handler, String message){
        this.handler = handler;
        this.message = message;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        execute(handler, message);
    }

    private void execute(IMessageHandler handler, String message){
        //消息处理
        handler.handleMessage(message);
    }
}

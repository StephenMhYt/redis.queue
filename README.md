# redis.queue
* 设计目的   
> 有些时候，项目本身在还没有集成MQ，但又需要用到消息队列的情况下，可使用redis来实现相关功能，可用于分布式系统。<br>
* 设计思路   
> 利用redis的订阅发布模式和生产者消费者模式相结合，消息生产者将具体消息放入redis队列后，同时将redis队列名称发布到指定channel，消费者订阅对应channel。> 当消费者接收到发布者发布的消息后，从指定的队列中获取相应的消息，进行处理。
* 不足之处    
> 由于redis的发布订阅模式的限制，当订阅者应某些原因导致服务宕机，在此期间发布者发布的消息将会丢失。



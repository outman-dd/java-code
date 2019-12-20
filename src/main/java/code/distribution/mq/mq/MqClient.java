package code.distribution.mq.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
public class MqClient {

    private final Map<String/*msgId*/, WrappedMessage> preMessages = new HashMap<>();

    private final Map<String/*msgId*/, WrappedMessage> messages = new HashMap<>();

    private ScheduledExecutorService prepareMsgScheduler = new ScheduledThreadPoolExecutor(1);

    private ScheduledExecutorService messageScheduler = new ScheduledThreadPoolExecutor(1);

    private static MqClient instance = new MqClient();

    public static MqClient getInstance(){
        return instance;
    }

    private MqDispatcher mqDispatcher = MqDispatcher.getInstance();

    public MqClient(){
        prepareMsgScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                preMessages.forEach((msgId, message) ->{
                    long now = System.currentTimeMillis();
                    if((now - message.getCreateTimestamp()) >= 180000){
                        boolean success = mqDispatcher.txCallback(msgId, message);
                        if(success){
                            commit(msgId);
                        }else{
                            rollback(msgId);
                        }
                    }
                });
            }
        }, 5, 10, TimeUnit.SECONDS);
        messageScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                messages.forEach((msgId, message) ->{
                    boolean success = mqDispatcher.send(msgId, message);
                    if(success){
                        ack(msgId);
                    }
                });
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * 准备消息
     * @param bizKey
     * @param topic
     * @param param
     * @return
     */
    public String prepare(String bizKey, String topic, Object param){
        //保存待发送消息
        String msgId = UUID.randomUUID().toString();
        preMessages.put(msgId, new WrappedMessage(bizKey, topic, param));
        return msgId;
    }

    /**
     * 提交消息
     * @param msgId
     * @return
     */
    public boolean commit(String msgId){
        //提交消息，触发消息投递
        WrappedMessage message = preMessages.get(msgId);
        if(message != null){
            messages.put(msgId, message);
            preMessages.remove(msgId);
        }
        return true;
    }

    /**
     * 回滚消息
     * @param msgId
     * @return
     */
    public boolean rollback(String msgId){
        //删除消息
        preMessages.remove(msgId);
        return true;
    }

    /**
     * ack
     * @param msgId
     * @return
     */
    public void ack(String msgId){
        messages.remove(msgId);
    }

}

package code.distribution.mq.stock;

import code.distribution.mq.mq.WrappedMessage;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
public class StockMqListener {

    private StockService stockService = StockService.getInstance();

    private static StockMqListener instance = new StockMqListener();

    public static StockMqListener getInstance(){
        return instance;
    }

    public boolean onMsgReceive(String msgId, WrappedMessage message){
        return false;
    }

    public boolean txMsgCallback(String msgId, WrappedMessage message){
        return stockService.preReduceCallback(message.getBizKey());
    }

}

package code.distribution.mq.mq;

import code.distribution.mq.common.TopicConst;
import code.distribution.mq.stock.StockService;
import code.distribution.mq.trade.TradeMqListener;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/27
 */
public class MqDispatcher {

    private TradeMqListener tradeMqListener = TradeMqListener.getInstance();

    private StockService stockService = StockService.getInstance();

    private static MqDispatcher instance = new MqDispatcher();

    public static MqDispatcher getInstance(){
        return instance;
    }

    public boolean txCallback(String msgId, WrappedMessage message){
        if(TopicConst.TRADE_ORDER_CREATE.equals(message.getTopic())){
            return stockService.preReduceCallback(message.getBizKey());
        }
        return false;
    }

    public boolean send(String msgId, WrappedMessage message) {
        if(TopicConst.TRADE_ORDER_CREATE.equals(message.getTopic())){
            return tradeMqListener.onMsgReceive(msgId, message);
        }
        return false;
    }
}

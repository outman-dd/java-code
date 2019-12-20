package code.distribution.mq.trade;

import code.distribution.mq.mq.WrappedMessage;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
public class TradeMqListener {

    private TradeService tradeService = TradeService.getInstance();

    private static TradeMqListener instance = new TradeMqListener();

    public static TradeMqListener getInstance(){
        return instance;
    }

    public boolean onMsgReceive(String msgId, WrappedMessage message){
        return tradeService.createOrder(message.getBizKey());
    }

    public boolean txMsgCallback(String msgId, WrappedMessage message){
        return false;
    }


}

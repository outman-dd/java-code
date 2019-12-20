package code.distribution.mq.stock;

import code.distribution.mq.BuyReq;
import code.distribution.mq.common.TopicConst;
import code.distribution.mq.mq.MqClient;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
public class StockService {

    private static AtomicInteger productStock = new AtomicInteger();

    private static ConcurrentHashMap<String/*orderNo*/, PreReduceStockFlow> preReduceFlowMap = new ConcurrentHashMap<>();

    private MqClient mqClient = MqClient.getInstance();

    private static StockService instance = new StockService();

    public static StockService getInstance(){
        return instance;
    }

    public StockService(){
        productStock = new AtomicInteger(100);
    }

    /**
     * 预减库存流水
     * @param orderNo
     * @param buyReq
     * @return
     */
    public boolean preReduce(String orderNo, BuyReq buyReq){
        if(!preCheck(buyReq.getCount())){
            //库存不足
            return false;
        }

        //准备发布创建交易订单消息
        String msgId = mqClient.prepare(orderNo, TopicConst.TRADE_ORDER_CREATE, buyReq);
        if(msgId == null){
            //系统异常
            return false;
        }

        preReduceFlowMap.putIfAbsent(orderNo, new PreReduceStockFlow(orderNo, "", buyReq.getCount(), new Date()));
        System.out.println(String.format("库存预减流水, orderNo=%s, count=%d", orderNo, buyReq.getCount()));

        //3、发布创建交易订单消息
        mqClient.commit(msgId);

        return true;
    }

    private boolean preCheck(int count) {
        AtomicInteger preReduceCount = new AtomicInteger();
        preReduceFlowMap.entrySet().forEach(entry -> {
            preReduceCount.addAndGet(entry.getValue().getCount());
        });
        return (productStock.get() - preReduceCount.get()) >= count;
    }

    /**
     * 预减库存回调
     * @return
     */
    public boolean preReduceCallback(String orderNo){
        PreReduceStockFlow reduceStockFlow = preReduceFlowMap.get(orderNo);
        return reduceStockFlow != null;
    }
}

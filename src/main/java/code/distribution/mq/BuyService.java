package code.distribution.mq;

import code.distribution.mq.pay.PayService;
import code.distribution.mq.stock.StockService;
import code.distribution.mq.trade.TradeOrder;
import code.distribution.mq.trade.TradeService;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 〈购买服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
public class BuyService {

    private StockService stockService = StockService.getInstance();

    private TradeService tradeService = TradeService.getInstance();

    private PayService payService = PayService.getInstance();

    public void buy(BuyReq buyReq) {
        Pair<Boolean, String> orderRet = order(buyReq);
        if(orderRet.getKey()){
            String orderNo = orderRet.getValue();
            createPayOrder(orderNo);
        }else{
            System.out.println(orderRet.getValue());
        }
    }

    /**
     * 下单
     * @param buyReq
     * @return
     */
    public Pair<Boolean, String> order(BuyReq buyReq){
        //1、生成交易流水号（订单编号）
        String orderNo = tradeService.preCreateOrder(buyReq);

        //2、预减库存，写库存预减流水
        boolean preReduce = stockService.preReduce(orderNo, buyReq);
        if(!preReduce){
            //库存不足
            tradeService.deleteOrder(orderNo, "库存不足,无法下单");
            return Pair.of(false, "库存不足,无法下单");
        }

        //5、创建交易订单
        tradeService.createOrder(orderNo);

        return Pair.of(true, orderNo);
    }

    /**
     * 付款
     */
    public boolean createPayOrder(String orderNo){
        TradeOrder order = tradeService.queryOrder(orderNo);
        if(order == null || !order.getIsVisible()){
            return false;
        }
        String payOrderNo = payService.createPayOrder(orderNo, order.getAmount());
        System.out.println("支付订单号:"+payOrderNo);
        return true;
    }

}

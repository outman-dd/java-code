package code.distribution.mq.trade;

import code.distribution.mq.BuyReq;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
public class TradeService {

    private static ConcurrentHashMap<String, TradeOrder> orderMap = new ConcurrentHashMap<>();

    private static TradeService instance = new TradeService();

    public static TradeService getInstance(){
        return instance;
    }

    /**
     * 预创建订单
     * @param buyReq
     * @return
     */
    public String preCreateOrder(BuyReq buyReq){
        String orderNo = newOrderNo(buyReq.getBuyerId());
        if(exist(orderNo)){
            return orderNo;
        }
        orderMap.putIfAbsent(orderNo, new TradeOrder(orderNo, buyReq.getBuyerId(), buyReq.getAmount()));
        System.out.println(String.format("预创建交易订单，orderNo=%s", orderNo));
        return orderNo;
    }

    private String newOrderNo(String buyerId){
        return "ORD"+System.nanoTime()+buyerId;
    }

    /**
     * 创建订单，设置为可见
     * @param orderNo
     * @return
     */
    public boolean createOrder(String orderNo) {
        TradeOrder preOrder = orderMap.get(orderNo);
        //订单不存在
        if(preOrder == null){
            return true;
        }

        //设置为可见，状态设置为待支付
        if(!preOrder.getIsVisible() && OrderStatus.INIT.name().equals(preOrder.getOrderStatus())) {
            preOrder.setOrderStatus(OrderStatus.TO_PAY.name());
            preOrder.setIsVisible(true);
        }
        return true;
    }

    private boolean exist(String orderNo) {
        return orderMap.contains(orderNo);
    }

    /**
     * 订单作废
     * @param orderNo
     * @return
     */
    public boolean deleteOrder(String orderNo, String reason){
        TradeOrder preOrder = orderMap.get(orderNo);
        //订单不存在
        if(preOrder == null){
            return true;
        }

        if(!preOrder.getIsVisible() && OrderStatus.INIT.name().equals(preOrder.getOrderStatus())){
            preOrder.setOrderStatus(OrderStatus.DELETED.name());
            preOrder.setRemark(reason);
        }
        return true;
    }

    /**
     * 订单取消
     * @param orderNo
     * @return
     */
    public boolean cancelOrder(String orderNo, String reason){
        TradeOrder preOrder = orderMap.get(orderNo);
        //订单不存在
        if(preOrder == null){
            return true;
        }
        return true;
    }

    /**
     * 查询订单
     * @param orderNo
     * @return
     */
    public TradeOrder queryOrder(String orderNo){
        return orderMap.get(orderNo);
    }
}

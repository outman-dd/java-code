package code.distribution.mq.pay;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
public class PayService {

    private final ConcurrentHashMap<String, PayOrder> payOrderMap = new ConcurrentHashMap<>();

    private static PayService instance = new PayService();

    public static PayService getInstance(){
        return instance;
    }

    public String createPayOrder(String bizOrderNo, BigDecimal amount){
        PayOrder payOrder = payOrderMap.get(bizOrderNo);
        if(payOrder != null){
            return payOrder.getOrderNo();
        }
        //生成支付订单
        PayOrder ordPlayOrder = payOrderMap.putIfAbsent(bizOrderNo, new PayOrder(newPayOrderNo(), "TB", bizOrderNo, amount));
        payOrder = payOrderMap.get(bizOrderNo);
        if(ordPlayOrder == null){
            System.out.println("创建新支付订单，"+payOrder);
        }else{
            System.out.println("创建新支付订单2，"+payOrder);
        }
        return payOrder.bizOrderNo;
    }

    private String newPayOrderNo(){
        return "P2019"+System.nanoTime();
    }
}

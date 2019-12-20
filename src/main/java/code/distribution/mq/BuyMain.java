package code.distribution.mq;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/27
 */
public class BuyMain {

    private static BuyService buyService = new BuyService();

    public static void main(String[] args) throws InterruptedException {
        BuyReq buyReq = new BuyReq();
        buyReq.setProductId("1001");
        buyReq.setAmount(new BigDecimal("100.00"));
        buyReq.setCount(1);
        buyReq.setBuyerId("1234");
        buyService.buy(buyReq);

        Thread.sleep(300000);
    }
}

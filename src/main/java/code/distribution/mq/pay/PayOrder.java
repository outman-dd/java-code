package code.distribution.mq.pay;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
@Data
public class PayOrder implements Serializable{

    String orderNo;

    String sourcePlatform;

    String bizOrderNo;

    BigDecimal amount;

    public PayOrder(String orderNo, String sourcePlatform, String bizOrderNo, BigDecimal amount) {
        this.orderNo = orderNo;
        this.sourcePlatform = sourcePlatform;
        this.bizOrderNo = bizOrderNo;
        this.amount = amount;
    }
}

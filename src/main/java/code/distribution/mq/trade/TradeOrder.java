package code.distribution.mq.trade;

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
public class TradeOrder implements Serializable{

    private String orderNo;

    private String buyerId;

    private BigDecimal amount;

    private String orderStatus;

    private Boolean isVisible;

    private String payOrderNo;

    private String remark;

    public TradeOrder(String orderNo, String buyerId, BigDecimal amount) {
        this.orderNo = orderNo;
        this.buyerId = buyerId;
        this.amount = amount;
        this.orderStatus = OrderStatus.INIT.name();
        this.isVisible = false;
    }

}

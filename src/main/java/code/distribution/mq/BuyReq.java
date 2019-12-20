package code.distribution.mq;

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
public class BuyReq implements Serializable{

    private String productId;

    private String buyerId;

    private BigDecimal amount;

    private int count;

}

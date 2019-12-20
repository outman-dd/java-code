package code.distribution.mq.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
@Data
@AllArgsConstructor
public class PreReduceStockFlow implements Serializable{

    private String orderNo;

    private String productId;

    private int count;

    private Date createTime;
}

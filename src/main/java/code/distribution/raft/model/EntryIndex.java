package code.distribution.raft.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 〈日志index对象〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/12
 */
@Data
@AllArgsConstructor
public class EntryIndex {

    private String nodeId;

    private int index;
}

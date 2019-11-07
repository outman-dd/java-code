package code.distribution.raft.enums;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-05
 */
public interface RequestType {

    int APPEND_ENTRIES = 1;

    int REQUEST_VOTE = 2;

    int CLEINT_REQ = 3;
}

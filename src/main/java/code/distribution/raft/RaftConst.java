package code.distribution.raft;

/**
 * 〈常量〉<p>
 *
 * @author zixiao
 * @date 2019/3/7
 */
public interface RaftConst {

    /**
     * 选举定时器
     * 必须要大于5倍心跳定时，建议是10倍关系
     */
    long ELECTION_TIMEOUT_MS = 1000;

    /**
     * 心跳定时器
     */
    long HEARTBEAT_MS = 100;

    /**
     * 空对象的term值
     */
    int EMPTY_TERM = -1;

}

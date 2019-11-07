package code.distribution.raft;

/**
 * 〈常量〉<p>
 *
 * @author zixiao
 * @date 2019/3/7
 */
public interface RaftConst {

    /**
     * 心跳定时器
     */
    long HEARTBEAT_MS = 100;

    /**
     * 选举定时器
     * 必须要大于5倍心跳定时，建议是10倍关系
     */
    long ELECTION_TIMEOUT_MS = 1000;

    /**
     * 空对象的term值
     */
    int EMPTY_TERM = -1;

    /**
     * 重试日志复制
     */
    long RETRY_APPEND_MS = 3000;

    /**
     * 换行符
     */
    String LINE_SEP =  System.getProperty("line.separator");

}

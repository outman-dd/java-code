package code.distribution.raft;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public interface ISender<Req, Ret> {

    Ret send(String nodeId, Req req);
}

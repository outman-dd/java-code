package code.distribution.raft;

/**
 * 〈RPC 处理者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public interface IHandler<REQ, RET> {

    RET handle(REQ req);
}

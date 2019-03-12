package code.distribution.raft.enums;

/**
 * 〈角色〉<p>
 * 所有服务器：
 如果commitIndex > lastApplied，那么就 lastApplied 加一，并把log[lastApplied]应用到状态机中（5.3 节）
 如果接收到的 RPC 请求或响应中，任期号T > currentTerm，那么就令 currentTerm 等于 T，并切换状态为跟随者（5.1 节）
 *
 * @author zixiao
 * @date 2019/3/11
 */
public enum RoleType {

    /**
     * 跟随者：
     响应来自候选人和领导者的请求
     如果在超过选举超时时间的情况之前都没有收到领导人的心跳，或者是候选人请求投票的，就自己变成候选人
     */
    FOLLOWER,

    /**
     * 候选人：
     在转变成候选人后就立即开始选举过程
         自增当前的任期号（currentTerm）
         给自己投票
         重置选举超时计时器
         发送请求投票的 RPC 给其他所有服务器
     如果接收到大多数服务器的选票，那么就变成领导人
     如果接收到来自新的领导人的附加日志 RPC，转变成跟随者
     如果选举过程超时，再次发起一轮选举
     */
    CANDIDATE,

    /**
     * 领导人：
     一旦成为领导人：发送空的附加日志 RPC（心跳）给其他所有的服务器；在一定的空余时间之后不停的重复发送，以阻止跟随者超时（5.2 节）
     如果接收到来自客户端的请求：附加条目到本地日志中，在条目被应用到状态机后响应客户端（5.3 节）
     如果对于一个跟随者，最后日志条目的索引值大于等于 nextIndex，那么：发送从 nextIndex 开始的所有日志条目：
        如果成功：更新相应跟随者的 nextIndex 和 matchIndex
        如果因为日志不一致而失败，减少 nextIndex 重试
     如果存在一个满足N > commitIndex的 N，并且大多数的matchIndex[i] ≥ N成立，并且log[N].term == currentTerm成立，那么令 commitIndex 等于这个 N （5.3 和 5.4 节）
     */
    LEADER
}

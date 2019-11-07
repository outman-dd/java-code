package code.distribution.raft.client;

import code.distribution.raft.RaftConfig;
import code.distribution.raft.kv.KvCommand;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-06
 */
public class RaftClient {

    private RpcClientService rpcClientService = new RpcClientService();

    private String leader;

    private List<String> clusterNodes = new ArrayList<>();

    public RaftClient(RaftConfig raftConfig){
        for (String node : raftConfig.parseClusterNodes()) {
            clusterNodes.add(node);
        }
    }

    public ClientRet invoke(ClientReq req) {
        return rpcClientService.invoke(getLeader(), req);
    }

    public ClientRet invoke(String nodeId, ClientReq req){
        return rpcClientService.invoke(nodeId, req);
    }

    public String getLeader(){
        if(leader == null){
            this.leader = lookupLeader();
        }
        return leader;
    }

    private String lookupLeader() {
        ClientReq req = new ClientReq(true, KvCommand.buildGet(""));
        String node = clusterNodes.get(RandomUtils.nextInt(0, clusterNodes.size()));
        ClientRet ret = rpcClientService.invoke(node, req);
        if (ret == null) {
            return null;
        }
        if (ret.isSuccess()) {
            return node;
        } else if (ret.getLeaderId() != null) {
            return ret.getLeaderId();
        }
        return null;
    }

    public void refresh(){
        this.leader = lookupLeader();
        System.out.println("Leader is " + leader);
    }

    public String randomNode(){
        return clusterNodes.get(RandomUtils.nextInt(0, clusterNodes.size()));
    }

}

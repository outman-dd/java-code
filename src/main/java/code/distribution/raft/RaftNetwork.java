package code.distribution.raft;

import java.util.*;

/**
 * 〈维护节点列表〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/7
 */
public class RaftNetwork {

    private static Map<String, RaftNodeServer> nodeMap = new HashMap<>();

    public static void register(String nodeId, RaftNodeServer nodeServer){
        nodeMap.put(nodeId, nodeServer);
    }

    public static Set<String> clusterNodeIds(String selfId){
        Set<String> nodeIds = new HashSet<>();
        Iterator<String> iterator = nodeMap.keySet().iterator();
        while (iterator.hasNext()){
            String nodeId = iterator.next();
            if(!selfId.equals(nodeId)){
                nodeIds.add(nodeId);
            }
        }
        return nodeIds;
    }

    public static int nodeNum(){
       return nodeMap.size();
    }

    public static RaftNodeServer nodeServer(String nodeId){
        return nodeMap.get(nodeId);
    }

    public static void offline(String nodeId){
        nodeMap.remove(nodeId);
    }

}

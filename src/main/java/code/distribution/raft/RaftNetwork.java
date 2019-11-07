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

    private static Set<String> nodeSet = new HashSet<>();

    public static void config(String ... nodeIds){
        for (String nodeId : nodeIds) {
            nodeSet.add(nodeId);
        }
    }

    public static Set<String> clusterNodeIds(String selfId){
        Set<String> nodeIds = new HashSet<>();
        Iterator<String> iterator = nodeSet.iterator();
        while (iterator.hasNext()){
            String nodeId = iterator.next();
            if(!selfId.equals(nodeId)){
                nodeIds.add(nodeId);
            }
        }
        return nodeIds;
    }

    public static int nodeNum(){
       return nodeSet.size();
    }
}

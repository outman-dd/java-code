package code.distribution.raft.boot;

import code.distribution.raft.RaftConfig;
import code.distribution.raft.RaftNodeServer;
import code.distribution.raft.fsm.StateMachine;
import code.distribution.raft.kv.KvStateMachine;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-05
 */
public class RaftBootstrap {

    /**
     * java code.distribution.raft.boot.RaftBootstrap 127.0.0.1:2001 127.0.0.1:2001,127.0.0.1:2002,127.0.0.1:2003
     *
     * @param args
     */
    public static void main(String[] args) {
        String nodeId = args[0];
        String clusterNodes = args[1];

        StateMachine stateMachine = new KvStateMachine();
        RaftNodeServer raftNodeServer = new RaftNodeServer(nodeId, stateMachine);

        RaftConfig raftConfig = new RaftConfig();
        raftConfig.setClusterNodes(clusterNodes);
        raftNodeServer.initConfig(raftConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                raftNodeServer.close();
            }
        }));
        raftNodeServer.start();
    }
}

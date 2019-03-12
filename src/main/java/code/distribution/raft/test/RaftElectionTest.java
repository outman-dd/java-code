package code.distribution.raft.test;

import code.distribution.raft.RaftNodeServer;
import code.distribution.raft.enums.RoleType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈Raft 选举测试〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class RaftElectionTest {

    @Test
    public void testElection() throws InterruptedException {
        int n = 5;
        List<RaftNodeServer> nodeServerList = new ArrayList<>(n);
        for(int i=0; i<n; i++){
            nodeServerList.add(new RaftNodeServer("NS-"+i));
        }

        for (RaftNodeServer nodeServer : nodeServerList) {
            nodeServer.start();
        }

        Thread.sleep(600 * 1000);
        for (RaftNodeServer nodeServer : nodeServerList) {
            nodeServer.close();
        }
    }

    @Test
    public void testElectionWithLeaderDown() throws InterruptedException {
        int n = 5;
        List<RaftNodeServer> nodeServerList = new ArrayList<>(n);
        for(int i=0; i<n; i++){
            nodeServerList.add(new RaftNodeServer("NS-"+i));
        }

        for (RaftNodeServer nodeServer : nodeServerList) {
            nodeServer.start();
        }

        //5s后 leader挂掉
        Thread.sleep(5 * 1000);
        for (RaftNodeServer nodeServer : nodeServerList) {
            if(nodeServer.getNode().getRole() == RoleType.LEADER){
                nodeServer.close();
            }
        }

        Thread.sleep(600 * 1000);

        for (RaftNodeServer nodeServer : nodeServerList) {
            nodeServer.close();
        }
    }
}

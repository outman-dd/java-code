package code.distribution.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈ZooKeeper实现分布式锁〉<p>
 *
 * 算法思路：临时顺序节点实现共享锁的实现
 * 对于加锁和获取锁操作
 * 1、让所有客户端都去/lock目录下创建临时顺序节点
 * 2、获取/lock所有子节点，判断自身创建节点序列号是否为最小的节点，如果是，则获得锁；否则，监视比自己创建的节点小的最大节点，进入等待。
 * 3、监听到 比自己创建的节点小的最大节点 的删除事件，则重复第2步，直到获取锁
 *
 * 对于解锁操作，只需要将自身创建的节点删除即可。
 *
 * 特点：利用临时顺序节点来实现分布式锁机制其实就是一种按照创建顺序排队的实现。
 * 这种方案效率高，避免了“惊群”效应，多个客户端共同等待锁，当锁释放时只有一个客户端会被唤醒。
 *
 * @author zixiao
 * @date 18/3/20
 */
public class ZooKeeperLock implements DistributedLock {

    private final ZkClient zkClient;

    private final String ROOT = "/dsLocks";

    private final String SEP = "/";

    public ZooKeeperLock(){
        zkClient = new ZkClient("zk.tbj.com:2181", 6000, 6000);
    }

    private ThreadLocal<String> lockSeqHolder = new ThreadLocal<>();

    @Override
    public void lock(String key) {
        String lockPath = ROOT + SEP +  key;
        //1、新建已key作为节点名的锁lockPath
        if(!zkClient.exists(lockPath)){
            try {
                zkClient.createPersistent(lockPath, true);
            } catch (ZkNodeExistsException e){
                //已存在
            }
        }

        //2、每个客户端在lockPath下创建临时顺序节点
        String myFullPath = zkClient.createEphemeralSequential(lockPath + SEP, null);
        String myLockSeq = myFullPath.substring(myFullPath.lastIndexOf(SEP)+1);

        //3、尝试获取锁
        tryGetLock(lockPath, myLockSeq);

        lockSeqHolder.set(myLockSeq);
    }

    /**
     * 尝试获取锁
     * 客户端获取lockPath下的子节点列表，判断自己创建的子节点是否为当前子节点列表中 序号最小 的子节点
     * @param lockPath
     * @param myLockSeq
     */
    private void tryGetLock(String lockPath, String myLockSeq){
        List<String> nodeSeqList = zkClient.getChildren(lockPath);
        Collections.sort(nodeSeqList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return new Integer(o1).compareTo(new Integer(o2));
            }
        });
        // a、如果是序号最小的子节点, 则认为获得锁
        if(nodeSeqList.size() == 0 || nodeSeqList.size() == 1 || myLockSeq.equals(nodeSeqList.get(0))){
            System.out.println(">>>Get lock on node " + myLockSeq);
            return;
        }

        // b、否则 监听序号比自己小的最大子节点的删除消息，获得子节点变更通知后 则重新尝试获取锁;
        String preNodeSeq = null;
        for(int i=0; i<nodeSeqList.size(); i++){
            if(myLockSeq.equals(nodeSeqList.get(i))){
                preNodeSeq = nodeSeqList.get(i-1);
            }
        }
        System.out.println(myLockSeq + "-->" + preNodeSeq);

        final CountDownLatch latch = new CountDownLatch(1);
        zkClient.subscribeDataChanges(lockPath + SEP + preNodeSeq, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                latch.countDown();
            }
        });

        //设置监视器前，节点已删除的情况，则重新尝试获取锁;
        if(!zkClient.exists(lockPath + SEP + preNodeSeq)){
            latch.countDown();
        }

        //阻塞等待节点删除通知
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        //继续判断第3步
        tryGetLock(lockPath, myLockSeq);
    }

    @Override
    public void unlock(String key) {
        String lockSeq = lockSeqHolder.get();
        String lockFullPath = ROOT + SEP +  key + SEP +lockSeq;
        if(zkClient.delete(lockFullPath)){
            lockSeqHolder.remove();
        }
        System.out.println(">>>Release lock on node " + lockSeq);
    }

    public static void main(String[] args) {

        //多个并发线程 模拟多个客户端
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for(int i=0; i<100; i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    DistributedLock lock = new ZooKeeperLock();
                    String bizKey = "loanId-5";
                    lock.lock(bizKey);
                    try {
                        doBiz(bizKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock(bizKey);
                    }
                }
            });
        }
        executorService.shutdown();

    }

    private static void doBiz(String bizKey) throws InterruptedException {
        Thread.sleep(new Random().nextInt(30));
    }

}

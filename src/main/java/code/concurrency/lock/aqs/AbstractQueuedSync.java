package code.concurrency.lock.aqs;

import code.concurrency.lock.cas.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

/**
 * 〈队列同步器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/14
 */
public class AbstractQueuedSync extends AbstractOwnableSync {

    private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            //AQS
            Class aqsClass = AbstractQueuedSync.class;
            stateOffset = unsafe.objectFieldOffset(aqsClass.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(aqsClass.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(aqsClass.getDeclaredField("tail"));
            //node
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    /**
     * head指向同步队列的头部，注意head为空结点，不存储信息
     */
    private volatile Node head;

    /**
     * tail则是同步队列的队尾
     */
    private volatile Node tail;

    /**
     * 同步状态，0代表锁未被占用，>=1代表锁已被占用
     */
    private volatile int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    /**
     * 〈节点〉<p>
     */
    static final class Node {

        //共享模式
        static final Node SHARED = new Node();

        //独占模式
        static final Node EXCLUSIVE = null;

        /**
         * 标识线程已处于结束状态
         */
        static final int CANCELLED =  1;

        /**
         * 等待被唤醒状态
         */
        static final int SIGNAL    = -1;

        /**
         * 条件状态，
         */
        static final int CONDITION = -2;

        /**
         * 在共享模式中使用表示获得的同步状态会被传播
         */
        static final int PROPAGATE = -3;

        /**
         * 初始化状态
         */
        static final int INITIAL = 0;

        /**
         * 等待状态
         * CANCELLED：值为1，在同步队列中等待的线程等待超时或被中断，需要从同步队列中取消该Node的结点，其结点的waitStatus为CANCELLED，即结束状态，进入该状态后的结点将不会再变化。
         * SIGNAL：值为-1，被标识为该等待唤醒状态的后继结点，当其前继结点的线程释放了同步锁或被取消，将会通知该后继结点的线程执行。说白了，就是处于唤醒状态，只要前继结点释放锁，就会通知标识为SIGNAL状态的后继结点的线程执行。
         * CONDITION：值为-2，与Condition相关，该标识的结点处于等待队列中，结点的线程等待在Condition上，当其他线程调用了Condition的signal()方法后，CONDITION状态的结点将从等待队列转移到同步队列中，等待获取同步锁。
         * PROPAGATE：值为-3，与共享模式相关，在共享模式中，该状态标识结点的线程处于可运行状态。
         * 0状态：值为0，代表初始化状态。
         */
        volatile int waitStatus;

        /**
         * 请求锁的线程
         */
        volatile Thread thread;

        /**
         * 同步队列中前继节点
         */
        volatile Node prev;

        /**
         * 同步队列中后继节点
         */
        volatile Node next;

        /**
         * 等待队列中的后继结点，与Condition有关
         */
        Node nextWaiter;

        /**
         * 判断是否为共享模式
         */
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null) {
                throw new NullPointerException();
            }else {
                return p;
            }
        }

        Node() {
        }

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "waitStatus=" + waitStatus +
                    ", thread=" + thread.getName() +
                    ", prev=" + prev +
                    ", next=" + next +
                    ", nextWaiter=" + nextWaiter +
                    '}';
        }
    }

    protected final boolean compareAndSetState(int expect, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    private final boolean compareAndSetHead(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, expect, update);
    }

    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    private final boolean compareAndWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapObject(node, waitStatusOffset, expect, update);
    }

    private final boolean compareAndWaitNext(Node node, int expect, int update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }

    /**
     * 请求锁
     * @param arg 锁数量
     */
    public final void acquire(int arg) {
        //尝试获取锁
        if(tryAcquire(arg)){
            return;
        }
        //放入等待队列
        Node waiter = addWaiter(Node.EXCLUSIVE);

        //队列中自旋获取锁，并在合适的位置park
        if (acquireQueued(waiter, arg)){
            //设置 待定过程中的 中断
            interruptSelf();
        }
    }

    /**
     * 插入到队尾
     * @param mode
     * @return
     */
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        Node currentTail = tail;
        if(currentTail != null){
            node.prev = currentTail;
            if(compareAndSetTail(currentTail, node)){
                currentTail.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    /**
     * CAS放到队尾
     * @param node
     * @return 前继节点
     */
    private Node enq(Node node) {
        for (;;){
            Node t = tail;
            //初始化
            if(t == null){
                if(compareAndSetHead(null, new Node())){
                    tail = head;
                }
            }else{
                if(compareAndSetTail(t, node)){
                    node.prev = t;
                    t.next = node;
                    return t;
                }
            }
        }
    }

    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted()){
            throw new InterruptedException();
        }
        if (tryAcquire(arg)){
            return;
        }
        //放入等待队列
        Node waiter = addWaiter(Node.EXCLUSIVE);

        //队列中自旋获取锁，并在合适的位置park，可被中断
        acquireQueuedInterruptibly(waiter, arg);
    }

    /**
     * 重新设置 等待过程的中断
     */
    private void interruptSelf(){
        Thread.currentThread().interrupt();
    }

    /**
     * 队列中自旋获取锁，并在合适的位置park
     * @param node
     * @param arg
     * @return 是否被中断过
     */
    private boolean acquireQueued(Node node, int arg) {
        boolean acquired = false;
        boolean interrupted = false;
        try{
            for(;;){
                Node pred = node.predecessor();
                //如果前继是头结点，则尝试获取锁
                if(pred == head && tryAcquire(arg)){
                    //拿到锁
                    acquired = true;
                    //设置为头结点
                    setAsHead(node);
                    return interrupted;
                }
                if(shouldWaitWhenPredNotHead(pred, node) && parkAndCheckInterrupt()){
                    interrupted = true;
                }
            }
        }finally {
            if(!acquired){
                cancelAcquire(node);
            }
        }
    }

    /**
     * 队列中自旋获取锁，并在合适的位置park，可被中断
     * @param node
     * @param arg
     * @return 是否被中断过
     */
    private void acquireQueuedInterruptibly(Node node, int arg) throws InterruptedException{
        boolean acquired = false;
        try{
            for(;;){
                Node pred = node.predecessor();
                //如果前继是头结点，则尝试获取锁
                if(pred == head && tryAcquire(arg)){
                    //拿到锁
                    acquired = true;
                    //设置为头结点
                    setAsHead(node);
                    return;
                }
                if(shouldWaitWhenPredNotHead(pred, node) && parkAndCheckInterrupt()){
                    throw new InterruptedException();
                }
            }
        }finally {
            if(!acquired){
                //
            }
        }
    }

    /**
     * park自己并返回中断标志
     * @return 返回中断标志
     */
    private boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        //返回中断状态，并清除中断标记
        return Thread.interrupted();
    }

    /**
     *
     * 判断当前结点的前驱结点的waitStatus
     * if 为SIGNAL状态(即等待唤醒状态)，则返回true。
     * if 为CANCELLED状态(值为1>0),即结束状态，则说明该前驱结点已没有用应该从同步队列移除，执行while循环，直到寻找到非CANCELLED状态的结点。
     * if 不为CANCELLED，也不为SIGNAL
     * (当从Condition的条件等待队列转移到同步队列时，结点状态为CONDITION 因此需要转换为SIGNAL)，那么将其转换为SIGNAL状态，等待被唤醒。
     * @param pred
     * @param node
     * @return
     */
    private boolean shouldWaitWhenPredNotHead(Node pred, Node node) {
        int predStatus = pred.waitStatus;
        if(predStatus == Node.SIGNAL){
            return true;
        }else if(predStatus == Node.CANCELLED){
            //遍历前驱结点直到找到没有结束状态的结点
            do{
                pred = pred.prev;
                node.prev = pred;
            }while (pred.waitStatus == Node.CANCELLED);
            pred.next = node;
        }else{
            //不是CANCELLED，也不为SIGNAL 设置为等待被唤醒
            compareAndWaitStatus(pred, predStatus, Node.SIGNAL);
        }
        return false;
    }

    /**
     *  设置会头结点
     * @param node
     */
    private void setAsHead(Node node) {
        head.next = null;
        head = node;
        node.prev = null;
        node.thread = null;
    }

    /**
     * 取消请求锁
     * @param node
     */
    private void cancelAcquire(Node node){
        //TODO 不完善
        if (node == null) {
            return;
        }

        node.thread = null;

        node.waitStatus = Node.CANCELLED;
        System.out.println("cancelAcquire : node="+node);
    }

    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            //唤醒后继节点
            Node h = head;
            if (h != null && h.waitStatus != Node.INITIAL){
                unparkSuccessor(h);
            }
            return true;
        }
        return false;
    }

    /**
     * unpark 后继节点
     * @param node
     */
    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if(ws < Node.INITIAL){
            compareAndWaitStatus(node, ws, Node.INITIAL);
        }

        Node successor = node.next;
        //正常情况，第一个后继节点就是待执行状态
        if(successor == null || successor.waitStatus == Node.CANCELLED){
            successor = null;
            //从尾部开始遍历 找到最后一个不是CANCELLED状态的节点
            for(Node t = tail; t != node && t != null; t = t.prev){
                if(t.waitStatus <= Node.INITIAL){
                    successor = t;
                }
            }
        }
        if(successor != null){
            LockSupport.unpark(successor.thread);
        }
    }

    /**
     * 是否有排队的前继节点
     * 即判断当前节点是否队列第一个
     */
    protected boolean hasQueuedPredecessors(){
        Node h = head;
        //队列为空
        if(tail == h){
            return false;
        }

        Node headNext = h.next;
        //没有头结点的后继节点，说明当前节点已经是head了
        if(headNext == null){
            return true;
        }
        //头结点的后继节点 线程== 当前线程，说明当前线程已经排在第一个了
        if(headNext.thread != Thread.currentThread()){
            return true;
        }
        return false;
    }

    /**
     * 独占模式下获取锁的方法
     * 立即返回
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    //独占模式下解锁的方法
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    //共享模式下获取锁的方法
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    //共享模式下解锁的方法
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    //判断是否为持有独占锁
    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

}

package code.collection.queue;

import code.concurrency.lock.cas.CasInteger;
import code.concurrency.lock.cas.UnsafeUtil;
import org.junit.Test;
import sun.misc.Unsafe;

/**
 * 〈循环队列〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public class CircularQueue<E> implements IQueue<E> {

    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    private static final long headOffset;

    private static final long tailOffset;

    static {
        try {
            headOffset = UNSAFE.objectFieldOffset
                    (CircularQueue.class.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset
                    (CircularQueue.class.getDeclaredField("tail"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    protected Object[] elements;

    protected int capacity;

    protected volatile int head;

    protected volatile int tail;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        //数组长度为容量+1，tail位置不存数据
        elements = new Object[this.capacity+1];
        head = tail = 0;
    }

    @Override
    public int size() {
        //tail>=head为正常情况；否则标识超过一圈，tail=数组大小+tail
        return tail >= head ? tail - head : (tail + elements.length - head);
    }

    @Override
    public boolean enqueue(E e) {
        if (isFull()) {
            System.out.println("Queue is full");
            return false;
        }

//        if (tail == capacity) {
//            //tail达到数据末尾, tail前进一位（到达数组0位置）
//            elements[tail] = e;
//            tail = 0;
//        } else {
//            //正常情况，tail位置插入数据，tail前进一位
//            elements[tail] = e;
//            tail++;
//        }
        elements[tail] = e;
        tail = (tail + 1) % elements.length;
        return true;
    }

    public boolean put(E e){
        if (isFull()) {
            System.out.println("Queue is full");
            return false;
        }

        int currentTail = tail;
        // CAS更新tail
        if (compareAndSetTail(currentTail, (currentTail + 1) % elements.length)) {
            // 非线程安全，tail后移一位，数据还未插入
            elements[currentTail] = e;
            return true;
        } else {
            return put(e);
        }
    }

    private boolean isFull(){
        //tail达到数组末尾，如果head还在0位置标识数组满了；tail小于head，则相差1代表数据满了
        return (tail == capacity && head == 0) || (tail < head && head - tail == 1);
    }

    @Override
    public E dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }

        E e = (E) elements[head];
        elements[head] = null;

//        if (head == capacity) {
//            //head在数组末尾，前进一位（到达数组0位置）
//            head = 0;
//        } else {
//            //正常情况，head前进一位
//            head++;
//        }
        head = (head + 1) % elements.length;
        return e;
    }


    public E take(){
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }

        int currentHead = head;
        // CAS更新head，线程安全
        if (compareAndSetHead(currentHead, (head + 1) % elements.length)) {
            return (E) elements[currentHead];
        } else {
            return take();
        }
    }

    private boolean isEmpty(){
        return head == tail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(head <= tail){
            for (int i = head; i < tail; i++) {
                sb.append(elements[i]).append("-");
            }
        } else {
            int realTail = elements.length + tail;
            for (int i = head; i < realTail; i++) {
                sb.append(elements[i]).append("-");
            }
        }
        return sb.toString();
    }

    public boolean compareAndSetHead(int except, int newValue){
        return UNSAFE.compareAndSwapInt(this, headOffset, except, newValue);
    }

    public boolean compareAndSetTail(int except, int newValue){
        return UNSAFE.compareAndSwapInt(this, tailOffset, except, newValue);
    }

    @Test
    public void test(){
        CircularQueue<String> circularQueue = new CircularQueue<>(4);
        System.out.print("1 enqueue A:");System.out.println(circularQueue.enqueue("A"));
        System.out.print("2 enqueue B:");System.out.println(circularQueue.enqueue("B"));
        System.out.print("3 enqueue C:");System.out.println(circularQueue.enqueue("C"));
        System.out.print("4 enqueue D:");System.out.println(circularQueue.enqueue("D"));
        System.out.print("5 enqueue E:");System.out.println(circularQueue.enqueue("E"));
        System.out.print("-------------- size=" + circularQueue.size() + ":");
        System.out.println(circularQueue);

        System.out.print("6 dequeue :");System.out.println(circularQueue.dequeue());
        System.out.print("7 enqueue F:");System.out.println(circularQueue.enqueue("F"));
        System.out.print("-------------- size=" + circularQueue.size() + ":");
        System.out.println(circularQueue);

        System.out.print("8 dequeue :");System.out.println(circularQueue.dequeue());
        System.out.print("9 dequeue :");System.out.println(circularQueue.dequeue());
        System.out.print("-------------- size=" + circularQueue.size() + ":");
        System.out.println(circularQueue);

        System.out.print("10 dequeue :");System.out.println(circularQueue.dequeue());
        System.out.print("11 dequeue :");System.out.println(circularQueue.dequeue());
        System.out.print("12 dequeue :");System.out.println(circularQueue.dequeue());
        System.out.print("-------------- size=" + circularQueue.size() + ":");
        System.out.println(circularQueue);
    }

    @Test
    public void testTake(){

    }

}

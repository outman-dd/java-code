package code.collection;

import java.util.concurrent.locks.LockSupport;

/**
 * 〈无锁RingBuffer〉<p>
 * 〈单线程读，单线程写〉
 *
 * @author zixiao
 * @date 19/1/17
 */
public class RingBuffer {

    /**
     * 环大小
     * 环存储容量 = 环大小 - 1
     */
    private final int bufferSize;

    private final String[] buffer;

    /**
     * 读取数据后，指针移到该数据的位置
     */
    private int head = 0;

    /**
     * 写入数据后，指针移到下一个空位置
     */
    private int tail = 0;

    public RingBuffer(int capacity) {
        this.bufferSize = capacity+1;
        this.buffer = new String[bufferSize];
    }

    public boolean isEmpty(){
        return head == tail;
    }

    public boolean isFull(){
        return head == nextIndex(tail);
    }

    /**
     * 单线程写
     * @param data
     * @return
     */
    public boolean write(String data){
        if(isFull()){
            return false;
        }
        buffer[tail] = data;
        tail = nextIndex(tail);
        return true;
    }

    /**
     * 单线程读
     * @return
     */
    public String read(){
        if(isEmpty()){
            return null;
        }
        String data = buffer[head];
        head = nextIndex(head);
        return data;
    }

    /**
     * 下一个位置
     * 如果next >= 环大小，则从0开始
     * @param current
     * @return
     */
    private int nextIndex(int current){
        int next = current + 1;
        if(next < bufferSize){
            return next;
        }else{
            return next-bufferSize;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final RingBuffer ringBuffer = new RingBuffer(1024);
        Thread reader = new Thread(() -> {
            int count  = 0;
            while (!Thread.currentThread().isInterrupted()){
                if(ringBuffer.read() == null){
                    LockSupport.parkNanos(50*1000L);
                }else {
                    count++;
                }
            }
            System.out.println("read count:"+count);
        });

        Thread writer = new Thread(() -> {
            int count  = 0;
            while (!Thread.currentThread().isInterrupted()){
                if(!ringBuffer.write(System.nanoTime() + "")){
                    LockSupport.unpark(reader);
                }else{
                    count++;
                }
            }
            System.out.println("write count:"+count);
        });

        //先起读线程，再起写线程
        reader.start();
        writer.start();

        //先停写线程，再停读线程
        Thread.sleep(1000);
        writer.interrupt();
        Thread.sleep(10);
        reader.interrupt();
    }

}

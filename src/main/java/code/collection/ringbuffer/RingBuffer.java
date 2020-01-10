package code.collection.ringbuffer;

/**
 * 〈环形缓冲区〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/23
 */
public class RingBuffer<E> {

    private int bufferSize;

    private Object[] entries;

    private final int indexMask;

    private final Sequencer sequencer;

    public RingBuffer(int bufferSize) {
        if (Integer.bitCount(bufferSize) != 1) {
            this.bufferSize = get2Power(bufferSize);
        } else {
            this.bufferSize = bufferSize;
        }
        this.indexMask = bufferSize - 1;
        this.entries = new Object[this.bufferSize];
        this.sequencer = new Sequencer(this.bufferSize);
    }

    private int get2Power(int capacity) {
        int n = capacity - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n + 1;
    }

    public E get(long seq){
        Long idx = seq & indexMask;
        return (E)entries[idx.intValue()];
    }

    public long next() {
        return sequencer.next();
    }

    public long next(int n) {
        return sequencer.next(n);
    }

}

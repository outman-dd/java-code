package code.collection.ringbuffer;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/23
 */
public class Sequencer {

    protected final int bufferSize;

    // 使用sequence代替，实现缓存行填充来消除伪共享
    private final AtomicLong cursor = new AtomicLong(-1);

    private final int[] availableBuffer;

    private final int indexMask;

    public Sequencer(int bufferSize) {
        this.bufferSize = bufferSize;
        if(Integer.bitCount(bufferSize) != 1){
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }
        availableBuffer = new int[this.bufferSize];
        indexMask = this.bufferSize - 1;
    }

    public long next(){
        return next(1);
    }

    public long next(int n) {
        long current = cursor.get();
        long next;
        do {
            next = current + n;
            // CAS申请n个位置
            if (cursor.compareAndSet(current, next)) {
                break;
            }
        } while (true);
        return next;
    }


}

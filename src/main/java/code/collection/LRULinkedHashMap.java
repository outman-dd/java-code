package code.collection;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 〈LRUCache〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/4
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1602075858487392174L;

    private int cacheSize;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LRULinkedHashMap(int cacheSize){
        super(cacheSize);
        this.cacheSize = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> var1) {
        return size() > cacheSize;
    }

    @Override
    public V get(Object o){
        try {
            lock.readLock().lock();
            return super.get(o);
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V put(K key, V value){
        try {
            lock.writeLock().lock();
            return super.put(key, value);
        }finally {
            lock.writeLock().unlock();
        }
    }


}

package code.cache.db;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 〈主从db〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/10/11
 */
public class MasterSlaveDb<K, V> implements IDb<K, V>{

    private SimpleDb<K, V> master = new SimpleDb<>();

    private SimpleDb<K, V> slave = new SimpleDb<>();

    private ScheduledExecutorService dbSync = new ScheduledThreadPoolExecutor(1);

    public MasterSlaveDb(){
        this(300);
    }

    public MasterSlaveDb(long syncDelay){
        dbSync.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                slave.getAll().forEach((k, v) -> {
                    if (master.get(k) == null) {
                        slave.remove(k);
                    }
                });
                master.getAll().forEach((k, v) -> {
                    slave.put(k, v);
                });
            }
        }, 100, syncDelay/2, TimeUnit.MILLISECONDS);
    }

    @Override
    public V get(K key) {
        return slave.get(key);
    }

    public V getFromMaster(K key) {
        return master.get(key);
    }

    @Override
    public V put(K key, V value) {
        return master.put(key, value);
    }

    @Override
    public V remove(K key) {
        return master.remove(key);
    }

    @Override
    public void close() {
        dbSync.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        IDb db = new MasterSlaveDb<String, Long>();
        String key = "1";
        db.put(key, System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            System.out.println(String.format("%s=>%s", key, db.get(key)));
        }

        System.out.println("-----------------");

        db.put(key, System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            System.out.println(String.format("%s=>%s", key, db.get(key)));
        }

        db.close();
    }
}

package code.concurrency.lock.aqs;

import java.io.Serializable;

/**
 * 〈AbstractOwnableSynchronizer〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/14
 */
public class AbstractOwnableSync implements Serializable{

    private volatile Thread exclusiveOwnerThread;

    protected void setExclusiveOwnerThread(Thread t){
        this.exclusiveOwnerThread = t;
    }

    protected Thread getExclusiveOwnerThread(){
        return this.exclusiveOwnerThread;
    }
}

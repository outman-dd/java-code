package code.distribution.at.common;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class SimpleSqlExecutor implements SqlExecutor{

    private String xid;
    private LockKey lockKey;

    public SimpleSqlExecutor(String xid, LockKey lockKey) {
        this.xid = xid;
        this.lockKey = lockKey;
    }

    public boolean execute(){
        return true;
    }

    public String beforeImage(){
        LockKey before = query();
        return "before:"+before;
    }

    private LockKey query(){
        LockKey before = lockKey.clone();
        return before;
    }

    public String afterImage(){
        return "after:"+lockKey;
    }
}

package code.distribution.transcation.tc;


import code.distribution.transcation.common.BranchStatus;
import code.distribution.transcation.common.GlobalStatus;
import code.distribution.transcation.common.LockKey;
import code.distribution.transcation.rm.ResourceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈事务协调器〉<p>
 * 〈维护全局和分支事务的状态，驱动全局事务的提交和回滚〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class DefaultTransactionCoordinator implements TransactionCoordinator {

    private static TransactionCoordinator instance = new DefaultTransactionCoordinator();

    private static ConcurrentHashMap<String, GlobalSession> globalSessionMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, List<BranchSession>> branchSessionMap = new ConcurrentHashMap<>();

    public static TransactionCoordinator getInstance() {
        return instance;
    }

    @Override
    public String beginGlobal() {
        GlobalSession globalSession = new GlobalSession();
        globalSessionMap.put(globalSession.getXid(), globalSession);
        return globalSession.getXid();
    }

    @Override
    public boolean commitGlobal(String xid) {
        GlobalSession globalSession = globalSessionMap.get(xid);
        if(globalSession == null){
            return false;
        }
        if(globalSession.getGlobalStatus() == GlobalStatus.PROCESSING){
            globalSession.setGlobalStatus(GlobalStatus.COMMITTING);

            List<BranchSession> branchSessions = branchSessionMap.get(xid);
            if(branchSessions != null){
                Iterator<BranchSession> iterator = branchSessions.iterator();
                while (iterator.hasNext()){
                    BranchSession branchSession = iterator.next();
                    if(commit(branchSession)){
                        iterator.remove();
                    }
                }
            }

            globalSession.setGlobalStatus(GlobalStatus.COMMITTED);
            return true;
        }else if(globalSession.getGlobalStatus() == GlobalStatus.COMMITTED){
            return true;
        }
        return false;
    }

    private boolean commit(BranchSession branchSession) {
        if(branchSession.getResourceManager().commitToTc(branchSession.getXid(), branchSession.getResourceId())){
            branchSession.setBranchStatus(BranchStatus.PHASE2_COMMITED);
            branchSession.unlock();
            return true;
        }else{
            branchSession.setBranchStatus(BranchStatus.PHASE2_COMMIT_FAILED);
            return false;
        }
    }

    @Override
    public boolean rollbackGlobal(String xid) {
        GlobalSession globalSession = globalSessionMap.get(xid);
        if(globalSession == null){
            return false;
        }
        if(globalSession.getGlobalStatus() == GlobalStatus.PROCESSING){
            globalSession.setGlobalStatus(GlobalStatus.ROLLBACKING);

            List<BranchSession> branchSessions = branchSessionMap.get(xid);
            if(branchSessions != null){
                Iterator<BranchSession> iterator = branchSessions.iterator();
                while (iterator.hasNext()){
                    BranchSession branchSession = iterator.next();
                    if(rollback(branchSession)){
                        iterator.remove();
                    }
                }
            }

            globalSession.setGlobalStatus(GlobalStatus.ROLLBACKED);
            return true;
        }else if(globalSession.getGlobalStatus() == GlobalStatus.ROLLBACKED){
            return true;
        }
        return false;
    }

    private boolean rollback(BranchSession branchSession) {
        if(branchSession.getResourceManager().rollbackToTc(branchSession.getXid(), branchSession.getResourceId())){
            branchSession.setBranchStatus(BranchStatus.PHASE2_ROLLBACKED);
            branchSession.unlock();
            return true;
        }else{
            branchSession.setBranchStatus(BranchStatus.PHASE2_ROLLBACK_FAILED);
            return false;
        }
    }

    @Override
    public boolean registerBranch(String xid, String resourceId, LockKey lockKey, ResourceManager rm){
        GlobalSession globalSession = globalSessionMap.get(xid);
        if(globalSession == null || globalSession.getGlobalStatus() != GlobalStatus.PROCESSING){
            return false;
        }

        BranchSession branchSession = new BranchSession(rm, xid, resourceId, lockKey);
        branchSession.setBranchStatus(BranchStatus.REGISTERED);

        if(!branchSession.lock()){
            return false;
        }

        List<BranchSession> branchSessions = branchSessionMap.get(xid);
        if(branchSessions == null){
            branchSessions = new ArrayList<>();
            branchSessions.add(branchSession);
            branchSessionMap.put(xid, branchSessions);
        }else{
            for (BranchSession session : branchSessions) {
                if(session.getResourceId().equals(branchSession.getResourceId())){
                    return true;
                }
            }
            branchSessions.add(branchSession);
        }
        return true;
    }

    @Override
    public boolean reportBranch(String xid, String resourceId, BranchStatus branchStatus) {
        GlobalSession globalSession = globalSessionMap.get(xid);
        if(globalSession == null || globalSession.getGlobalStatus() == GlobalStatus.ROLLBACKED
                || globalSession.getGlobalStatus() == GlobalStatus.COMMITTED){
            return false;
        }

        BranchSession branchSession = findBranch(xid, resourceId);
        if(branchSession != null){
            branchSession.setBranchStatus(branchStatus);
            return true;
        }
        return false;
    }

    private BranchSession findBranch(String xid, String resourceId){
        List<BranchSession> branchSessions = branchSessionMap.get(xid);
        if(branchSessions != null){
            for (BranchSession session : branchSessions) {
                if(session.getResourceId().equals(resourceId)){
                    return session;
                }
            }
        }
        return null;
    }

}

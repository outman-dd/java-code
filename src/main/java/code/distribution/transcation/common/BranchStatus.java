package code.distribution.transcation.common;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/26
 */
public enum BranchStatus {

    REGISTERED,

    PHASE1_DONE,

    PHASE1_FAIL,

    PHASE1_TIMEOUT,

    PHASE2_COMMITED,

    PHASE2_ROLLBACKED,

    PHASE2_COMMIT_FAILED,

    PHASE2_ROLLBACK_FAILED

}

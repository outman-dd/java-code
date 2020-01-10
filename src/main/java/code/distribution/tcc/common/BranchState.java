package code.distribution.tcc.common;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/8
 */
public enum BranchState {

    INITIAL,

    ONE_PHASE_OK,

    TWO_COMMIT_OK,

    TWO_COMMITTING,

    TWO_ROLLBACK_OK,

    TWO_ROLLING_BACK,

}

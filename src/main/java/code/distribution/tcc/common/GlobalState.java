package code.distribution.tcc.common;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
public enum GlobalState {

    PROCESSING,

//    ONE_PHASE_OK,
//
//    ONE_PHASE_FAIL,

    TWO_COMMITTING,

    TWO_COMMITTED,

    TWO_ROLLING_BACK,

    TWO_ROLLED_BACK
}

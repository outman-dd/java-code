package code.ratelimiter;

/**
 * 〈限速器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/28
 */
public interface RateLimiter {

    boolean canPass(int acquireCount);

}

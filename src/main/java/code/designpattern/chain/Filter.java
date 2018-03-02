package code.designpattern.chain;

/**
 * 〈过滤器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public interface Filter<T> {

    void doFilter(Request<T> request, Response response, FilterChain filterChain);
}

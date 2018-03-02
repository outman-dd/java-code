package code.designpattern.chain;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class EmptyFilter implements Filter<String> {

    public void doFilter(Request<String> request, Response response, FilterChain chain) {
        response.getResponse().append("-->EmptyFilter");

        chain.doFilter(request, response, chain);
    }
}
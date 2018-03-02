package code.designpattern.chain;

/**
 * 〈一句话功能简述〉<p>
 * 〈调用顺序〉
 * -->HtmlFilter-->SensitiveFilter-->EmptyFilter
 *
 * @author zixiao
 * @date 18/2/28
 */
public class FilterChainTest {

    public static void main(String[] args) {
        //设定过滤规则，对msg字符串进行过滤处理
        String msg = "我被就业了:):, <script>";
        Request<String> request = new Request<String>(msg);
        Response response = new Response();

        FilterChain filterChain = new FilterChain();
        filterChain.addFilter(new HtmlFilter())
                .addFilter(new SensitiveFilter())
                .addFilter(new EmptyFilter());

        filterChain.doFilter(request, response, filterChain);
        System.out.println(response);

    }
}

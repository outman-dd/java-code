package code.designpattern.chain;

/**
 * 〈一句话功能简述〉<p>
 * 〈处理字符串中的HTML标记〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class HtmlFilter implements Filter<String> {

    public void doFilter(Request<String> request, Response response,FilterChain chain) {
        //将字符串中出现的"<>"符号替换成"[]"
        String newString = request.getRequest().replace('<', '[').replace('>', ']');
        request.setRequest(newString);
        response.getResponse().append("-->HtmlFilter");

        chain.doFilter(request, response, chain);
    }

}

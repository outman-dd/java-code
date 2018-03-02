package code.designpattern.chain;

/**
 * 〈一句话功能简述〉<p>
 * 〈定义的过滤敏感字眼的过滤规则〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class SensitiveFilter implements Filter<String>{

    public void doFilter(Request<String> request, Response response,FilterChain chain) {
        //处理字符串中的敏感信息，将被就业和谐成就业
        String newString = request.getRequest().replace("被就业", "就业");
        request.setRequest(newString);
        response.getResponse().append("-->SensitiveFilter");

        chain.doFilter(request, response, chain);
    }

}
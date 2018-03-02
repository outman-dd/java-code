package code.designpattern.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈过滤器链〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class FilterChain implements Filter {

    //用List集合来存储过滤规则
    private List<Filter> filters = new ArrayList<Filter>();

    //用于标记规则的引用顺序
    int index = 0;

    //往规则链条中添加规则
    public FilterChain addFilter(Filter f) {
        filters.add(f);
        return this;
    }

    public void doFilter(Request request, Response response, FilterChain chain){
        if(index < filters.size()){
            //每执行一个过滤规则，index自增1
            Filter f = filters.get(index++);
            System.out.println(index + ": " +f);
            //根据索引值获取对应的规律规则对字符串进行处理
            f.doFilter(request, response, chain);

        }else{
            //最后一个过滤器调用
        }

    }

}

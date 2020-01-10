package code.distribution.tcc.rm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/8
 */
public class TccInvocationHandler implements InvocationHandler {

    private Object target;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("--- before ---");
        Object ret = method.invoke(target, args);
        System.out.println("--- after ---");
        return ret;
    }

    public <T> T getProxy(Class<T> clazz, Object target){
        this.target = target;
        Object service = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)service;
    }

}

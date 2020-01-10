package code.distribution.tcc.utils;

import code.distribution.tcc.common.TccAction;
import code.distribution.tcc.common.TxMethod;
import code.distribution.tcc.exception.TccException;

import java.lang.reflect.Method;

/**
 * 〈Tcc工具类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
public class TccUtils {

    public static TxMethod getTxMethod(Object object, Class clazz, String methodName, Object[] args){
        TccAction tccAction = TccUtils.getTccAction(clazz, methodName);
        if(tccAction == null){
            return null;
        }
        Method tryM = TccUtils.getMethod(clazz, tccAction.try1());
        if(tryM == null){
            throw new TccException(String.format("try方法'%s'不存在， class=%s", tccAction.try1(), clazz));
        }
        Method confirmM = TccUtils.getMethod(clazz, tccAction.confirm());
        if(tryM == null){
            throw new TccException(String.format("confirm方法'%s'不存在， class=%s", tccAction.confirm(), clazz));
        }
        Method cancelM = TccUtils.getMethod(clazz, tccAction.cancel());
        if(tryM == null){
            throw new TccException(String.format("cancel方法'%s'不存在， class=%s", tccAction.cancel(), clazz));
        }

        return new TxMethod(object, tryM, confirmM, cancelM, args);
    }

    public static TccAction getTccAction(Class clazz, String methodName){
        TccAction tccAction = null;
        for(Method method : clazz.getDeclaredMethods()){
            if(method.getName().equals(methodName)){
                tccAction = method.getAnnotation(TccAction.class);
                if(tccAction != null){
                    return tccAction;
                }
            }
        }
        return null;
    }

    public static Method getMethod(Class clazz, String methodName){
        for(Method method : clazz.getDeclaredMethods()){
            if(method.getName().equals(methodName)){
                return method;
            }
        }
        return null;
    }
}

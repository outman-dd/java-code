package code.distribution.tcc.common;

import lombok.Data;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
@Data
public class TxMethod implements Serializable {

    private Object object;

    private Method tryMethod;

    private Method confirmMethod;

    private Method cancelMethod;

    private Object[] args;

    public TxMethod(Object object, Method tryMethod, Method confirmMethod, Method cancelMethod, Object[] args) {
        this.object = object;
        this.tryMethod = tryMethod;
        this.confirmMethod = confirmMethod;
        this.cancelMethod = cancelMethod;
        this.args = args;
    }

    public boolean doTry(){
        return invoke(tryMethod);
    }

    public boolean doConfirm(){
        return invoke(confirmMethod);
    }

    public boolean doCancel(){
        return invoke(cancelMethod);
    }

    private boolean invoke(Method method){
        try {
            method.invoke(object, args);
            return true;
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        }
    }


}

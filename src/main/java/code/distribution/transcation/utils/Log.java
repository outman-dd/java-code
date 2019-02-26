package code.distribution.transcation.utils;

import code.distribution.transcation.common.RoleType;

import java.text.MessageFormat;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class Log {

    private String logPrefix;

    private Log(String logPrefix){
        this.logPrefix = logPrefix;
    }

    public static Log getLog(RoleType roleType){
        return new Log(roleType.name());
    }

    public void info(String msg ){
        System.out.println(Thread.currentThread().getName()+":"+logPrefix + " - " + msg);
    }

    public void info(String msg, Object ... arg1 ){
        System.out.println(Thread.currentThread().getName()+":"+logPrefix + " - " + MessageFormat.format(msg, arg1));
    }

    public void error(String msg, Throwable e){
        System.err.print(Thread.currentThread().getName()+":"+logPrefix + " - " + msg + ", error:");
        e.printStackTrace();
    }

}

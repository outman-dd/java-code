package code.distribution.transcation.utils;

import code.distribution.transcation.common.RoleType;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class Log {

    private String logType;

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.FFF");

    private Log(String logType){
        this.logType = logType;
    }

    public static Log getLog(RoleType roleType){
        return new Log(roleType.name());
    }

    public void info(String msg ){
        System.out.println(getLogPrefix() + msg);
    }

    public void info(String msg, Object ... arg1 ){
        System.out.println(getLogPrefix() + MessageFormat.format(msg, arg1));
    }

    public void error(String msg, Throwable e){
        System.err.print(getLogPrefix() + msg + ", error:");
        e.printStackTrace();
    }

    private String getLogPrefix(){
        return sdf.format(new Date())+ "-" + Thread.currentThread().getName() + ":" + logType + " - ";
    }

}

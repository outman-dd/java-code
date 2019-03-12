package code.distribution.raft.util;

import java.text.MessageFormat;

/**
 * 〈日志工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class RaftLogger {

    private String logPrefix;

    private RaftLogger(String logPrefix){
        this.logPrefix = logPrefix;
    }

    public static RaftLogger getLogger(String nodeId){
        return new RaftLogger(nodeId);
    }

    public void info(String msg ){
        System.out.println(getNowTime()+ logPrefix + " - " + msg);
    }

    public void info(String msg, Object ... arg1 ){
        System.out.println(getNowTime()+ logPrefix + " - " + MessageFormat.format(msg, arg1));
    }

    public void error(String msg, Throwable e){
        System.err.print(getNowTime()+ logPrefix + " - " + msg + ", error:");
        e.printStackTrace();
    }

    private String getNowTime(){
        return System.currentTimeMillis() + " ";
    }

}

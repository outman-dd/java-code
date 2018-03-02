package code.designpattern.command;

/**
 * 〈电视机 命令实现者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class TvReceiver {

    /**
     * 电视机 开启状态
     */
    private boolean tvStatus = false;

    public boolean turnOn(){
        System.out.println("Tv is turn on");
        tvStatus = true;
        return tvStatus;
    }

    public boolean turnOff(){
        System.out.println("Tv is turn off");
        tvStatus = false;
        return tvStatus;
    }

}

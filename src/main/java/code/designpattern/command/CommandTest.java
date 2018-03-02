package code.designpattern.command;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class CommandTest {

    public static void main(String[] args) throws InterruptedException {
        Invoker invoker = new Invoker(new CommandContext());

        //电视机命令接受者
        TvReceiver tvReceiver = new TvReceiver();

        //设置并调用开机命令
        invoker.setCommand(new TvOnCommand(tvReceiver));
        invoker.invoke();

        //3秒后关闭
        Thread.sleep(3000);

        //设置并调用关机命令
        invoker.setCommand(new TvOffCommand(tvReceiver));
        invoker.invoke();
    }
}

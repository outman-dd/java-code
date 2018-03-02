package code.designpattern.command;

/**
 * 〈电视机关闭命令〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class TvOffCommand implements Command<Boolean> {

    private TvReceiver tvReceiver;

    public TvOffCommand(TvReceiver tvReceiver) {
        this.tvReceiver = tvReceiver;
    }

    public Boolean execute(CommandContext commandContext) {
        return tvReceiver.turnOff();
    }
}

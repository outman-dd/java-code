package code.designpattern.command;

/**
 * 〈电视机打开命令〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class TvOnCommand implements Command<Boolean> {

    private TvReceiver tvReceiver;

    public TvOnCommand(TvReceiver tvReceiver) {
        this.tvReceiver = tvReceiver;
    }

    public Boolean execute(CommandContext commandContext) {
        return tvReceiver.turnOn();
    }
}

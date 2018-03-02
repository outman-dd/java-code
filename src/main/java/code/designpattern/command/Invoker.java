package code.designpattern.command;

/**
 * 〈命令的请求者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class Invoker {

    private Command command;

    private CommandContext commandContext;

    public Invoker(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    public void invoke(){
        command.execute(commandContext);
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}

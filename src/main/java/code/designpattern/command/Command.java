package code.designpattern.command;

/**
 * 〈命令接口〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public interface Command<T>{

    T execute(CommandContext commandContext);
}

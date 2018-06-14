package code.performance.cat;

/**
 * 〈命令接口〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/12
 */
public interface ICommand<T> {

    Result<T> execute();
}
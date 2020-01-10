package code.distribution.at.common;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public interface SqlExecutor {

    boolean execute();

    String beforeImage();

    String afterImage();
}

package code.distribution.tcc.common;

import java.lang.annotation.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TccAction {

    /**
     * 预占方法
     * @return
     */
    String try1();

    /**
     * 确认方法
     */
    String confirm();

    /**
     * 回滚方法
     */
    String cancel();
}

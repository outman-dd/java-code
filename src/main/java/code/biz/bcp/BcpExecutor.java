package code.biz.bcp;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/3
 */
public class BcpExecutor {

    private DataSeeker seeker = new TestDataSeeker();

    private Checker checker = new TestChecker();

    public boolean execute(Map<String, Object> left){
        Map<String, Object> right = seeker.seek(left);
        return checker.check(left, right);
    }

    public static void main(String[] args) {
        BcpExecutor bcpExecutor = new BcpExecutor();
        Map<String, Object> map = new HashMap<>();
        map.put("loanId", -1L);

        Assert.isTrue(bcpExecutor.execute(map), "对账不平");
    }
}

package code.biz.bcp;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/4
 */
public class TestEnv {

    public static void main(String[] args) {
        System.out.println("SYS_TTT:"+System.getenv("SYS_TTT"));
        System.getenv().forEach((key, value)->{
            System.out.println(key+" => "+value);
        });
    }

}

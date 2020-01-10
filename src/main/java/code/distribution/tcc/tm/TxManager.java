package code.distribution.tcc.tm;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/9
 */
public interface TxManager {

    String begin();

    boolean commit(String xid);

    boolean rollback(String xid);

}

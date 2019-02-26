package code.distribution.transcation.tm;

/**
 * 〈TM〉<p>
 * Transaction Manager(TM):
 * Define the scope of global transaction:
 * begin a global transaction, commit or rollback a global transaction.
 *
 * @author zixiao
 * @date 2019/2/25
 */
public interface TransactionManager {

    String begin();

    boolean commit(String xid);

    boolean rollback(String xid);

}

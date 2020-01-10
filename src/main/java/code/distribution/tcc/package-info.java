/**
 * 〈TCC模式的分布式事务〉<p>
 * 1 空回滚，try没执行，允许cancel空操作
 * 2 防悬挂，先cancel后，再try导致资源不释放
 * 3 幂等控制，多次confirm/cancel
 *
 * @author zixiao
 * @date 2020/1/7
 */
package code.distribution.tcc;
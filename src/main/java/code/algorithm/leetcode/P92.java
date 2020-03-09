package code.algorithm.leetcode;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/3/3
 */
public class P92 {

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (head == null || head.next == null) {
            return head;
        }
        if (m == n) {
            return head;
        }

        ListNode l = null;
        ListNode r = null;

        /**
         *  l    m              n
         *  A -> B -> C -> D -> E -> F -> NULL
         *  m位置作为反转第一个节点
         */
        ListNode mHead;
        if (m > 1) {
            l = head;
            for (int i = 2; i < m; i++) {
                l = l.next;
            }
            mHead = l.next;
        } else {
            mHead = head;
        }
        /**
         *  l    m              n
         *  A -> B -> C -> D -> E -> F -> NULL
         *       p    q   r
         */
        ListNode p = mHead;
        ListNode q = p.next;
        r = q.next;

        /**
         *  l    m              n
         *  A -> C <- B -> D -> E -> F -> NULL
         *       p    q   r
         */
        p.next = null;
        q.next = p;

        /**
         *  p,q,r指针前进，修改p和q的方向
         *  l    m              n
         *  A -> C <- B <- D -> E -> F -> NULL
         *            p    q   r
         *
         *  q达到n的位置 结束
         *
         *  l    m              n
         *  A -> C <- B <- D -> E -> F -> NULL
         *                 p    q   r
         */
        for (int i = m + 1; i < n; i++) {
            q.next = p;
            p = q;
            q = r;
            r = r.next;
        }

        /**
         *  l    m              n
         *  A -> C <- B <- D <- E -> F -> NULL
         *                 p    q   r
         */
        q.next = p;

        if (l != null) {
            l.next = q;
        } else {
            head = q;
        }
        mHead.next = r;

        mHead = l = r = p = q = null;
        return head;
    }

}

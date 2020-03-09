package code.algorithm.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/3/9
 */
public class P432 {

    static class Node{

        String key;

        int val;

        Node prev;

        Node next;

        public Node(String key, int val, Node prev, Node next) {
            this.key = key;
            this.val = val;
            this.prev = prev;
            this.next = next;
        }

        public Node(String key, int val) {
            this(key, val, null, null);
        }

    }

    private Map<String, Node> map = new HashMap<>();

    private Node head;

    private Node tail;

    /** Inserts a new key <Key> with value 1. Or increments an existing key by 1. */
    public void inc(String key) {
        Node node = map.get(key);
        if (node == null) {
            map.put(key, insert(key));
        } else {
            node.val += 1;
            refreshInc(node);
        }
    }

    private void refreshInc(Node node) {
        if(head == tail || node == tail){
            return;
        }
        Node cur = node.next;
        while (cur != null){
            if (cur.val < node.val) {
                cur = cur.next;
            } else {
                if (cur.prev != node) {
                    //插入cur之前
                    remove(node);
                    cur.prev.next = node;
                    node.prev = cur.prev;
                    node.next = cur;
                    cur.prev = node;
                }
                return;
            }
        }
        //插入尾部
        remove(node);
        tail.next = node;
        node.prev = tail;
        tail = node;
    }

    private Node insert(String key){
        Node node = new Node(key, 1);
        if (head == null) {
            tail = head = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        return node;
    }


    /** Decrements an existing key by 1. If Key's value is 1, remove it from the data structure. */
    public void dec(String key) {
        Node node = map.get(key);
        if(node != null){
            if(node.val == 1){
                remove(node);
                node = null;
                map.remove(key);
            }else{
                node.val -= 1;
                refreshDec(node);
            }
        }
    }

    private void refreshDec(Node node) {
        if(head == tail || node == head){
            return;
        }
        Node cur = node.prev;
        while (cur != null){
            if (cur.val > node.val) {
                cur = cur.prev;
            } else {
                if (cur.next != node) {
                    //插入cur之后
                    remove(node);
                    cur.next.prev = node;
                    node.next = cur.next;
                    node.prev = cur;
                    cur.next = node;
                    return;
                }
                return;
            }
        }
        //插入头部
        remove(node);
        head.prev = node;
        node.next = head;
        head = node;
    }

    private void remove(Node node){
        if(head == tail){
            head = tail = null;
        }else if(head == node){
            head = node.next;
            head.prev = null;
        }else if(tail == node){
            tail = node.prev;
            tail.next = null;
        }else{
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
        node.prev = null;
        node.next = null;
    }

    /** Returns one of the keys with maximal value. */
    public String getMaxKey() {
        if(tail != null){
            return tail.key;
        }
        return "";
    }

    /** Returns one of the keys with Minimal value. */
    public String getMinKey() {
        if(head != null){
            return head.key;
        }
        return "";
    }

    @Override
    public String toString() {
        if (head == null) {
            return "->NULL";
        }
        StringBuilder sb = new StringBuilder();
        Node node = head;
        while (node != null) {
            sb.append("<->").append(node.key).append("(").append(node.val).append(")");
            node = node.next;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        P432 link = new P432();
        link.inc("c");
        link.inc("b");
        link.inc("c");
        link.inc("a");
        link.inc("b");
        link.inc("c");
        System.out.println(link);

        link.dec("c");
        link.inc("a");
        link.inc("b");
        System.out.println(link);

        link = new P432();
        link.inc("b");
        link.inc("a");
        link.inc("b");
        link.inc("b");
        System.out.println(link);

        //["AllOne","inc","inc","inc","inc","inc","inc","dec", "dec","getMinKey","dec","getMaxKey","getMinKey"]
        //[[],["a"],["b"],["b"],["c"],["c"],["c"],["b"],["b"],[],["a"],[],[]]
        link = new P432();
        link.inc("a");
        link.inc("b");
        link.inc("b");
        link.inc("c");
        link.inc("c");
        link.inc("c");

        link.dec("b");
        link.dec("b");

        System.out.println(link.getMinKey());
        link.dec("a");
        System.out.println(link.getMaxKey());
        System.out.println(link.getMinKey());
    }

}

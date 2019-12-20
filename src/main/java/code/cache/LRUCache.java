package code.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈Least Recently Used Cache〉<p>
 * 〈HashMap和双向链表〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class LRUCache<K, V> implements ICache<K, V>{

    private int maxSize;

    private Map<K, DoubleLinkNode<K, V>> map = new HashMap<>();

    private DoubleLinkNode<K, V> head;

    private DoubleLinkNode<K, V> tail;

    public LRUCache(int maxSize){
        this.maxSize = maxSize;
    }

    @Override
    public V get(K key) {
        if(map.containsKey(key)){
            DoubleLinkNode<K, V> node = map.get(key);
            if(head != node){
                breakLink(node);
                addToHead(node);
            }
            return node.value;
        }
        return null;
    }

    /**
     * 断开节点的连接
     * @param node
     */
    private void breakLink(DoubleLinkNode node){
        DoubleLinkNode next = node.next;
        DoubleLinkNode prev = node.prev;
        if(next == null){
            prev.next = null;
            tail = prev;
        }else{
            prev.next = next;
            next.prev = prev;
        }
    }

    /**
     * 添加节点到头部
     * @param node
     */
    private void addToHead(DoubleLinkNode node){
        if(head == null){
            head = node;
        }else{
            node.next = head;
            head.prev = node;
            node.prev = null;
            head = node;
        }
    }

    /**
     * 删除tail
     */
    private void removeTail() {
        if(tail != null){
            DoubleLinkNode secondLast = tail.prev;
            secondLast.next = null;
            tail.next = null;
            tail.prev = null;
            tail = secondLast;
        }
    }

    /**
     * 删除head
     */
    private void removeHead(){
        if(head != null){
            DoubleLinkNode headNext = head.next;
            headNext.prev = null;
            head.next = null;
            head.prev = null;
            head = headNext;
        }
    }

    @Override
    public boolean exist(K key) {
        return map.containsKey(key);
    }

    @Override
    public V set(K key, V value) {
        if(map.containsKey(key)){
            DoubleLinkNode node = map.get(key);
            node.value = value;
            if(head != node){
                breakLink(node);
                addToHead(node);
            }
        }else{
            if(map.size() == maxSize){
                map.remove(tail.key);
                removeTail();
            }
            DoubleLinkNode node = new DoubleLinkNode(key, value);
            map.put(key, node);
            addToHead(node);
        }
        return value;
    }

    @Override
    public V set(K key, V value, int expireSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setnx(K key, V value) {
        if(map.containsKey(key)){
           return false;
        }
        set(key, value);
        return true;
    }

    @Override
    public void expire(K key, int expireSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V delete(K key) {
        if(map.containsKey(key)){
            DoubleLinkNode<K, V> node = map.get(key);
            map.remove(key);
            if(head != node){
                breakLink(node);
            }else{
                removeHead();
            }
            return node.value;
        }
        return null;
    }

    @Override
    public void stop() {
        map.clear();
        head = null;
        tail = null;
    }

    class DoubleLinkNode<K, V>{

        private K key;

        private V value;

        private DoubleLinkNode<K, V> prev;

        private DoubleLinkNode<K, V> next;

        public DoubleLinkNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DoubleLinkNode)) return false;

            DoubleLinkNode that = (DoubleLinkNode) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;
            if (prev != null ? !prev.equals(that.prev) : that.prev != null) return false;
            return !(next != null ? !next.equals(that.next) : that.next != null);

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            result = 31 * result + (prev != null ? prev.hashCode() : 0);
            result = 31 * result + (next != null ? next.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return '{' +
                    "key=" + key +
                    ", value=" + value +
                    ", prev=" + (prev!=null) +
                    ", next=" + (next!=null) +
                    '}';
        }
    }

    public String prettyPrint(){
        StringBuffer stringBuffer = new StringBuffer("LRUCache:");
        DoubleLinkNode node = head;
        while (true){
            stringBuffer.append(node.value);
            node = node.next;
            if(node != null){
                stringBuffer.append("=>");
            }else{
                break;
            }
        }
        stringBuffer.append(", head="+head).append(", tail="+tail);
        return stringBuffer.toString();
    }

    public int size(){
        return map.size();
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(4);
        lruCache.set("a", "a");
        lruCache.set("b", "b");
        lruCache.set("c", "c");
        lruCache.set("d", "d");
        System.out.println("add a,b,c,d " + lruCache.prettyPrint() + ", size:"+lruCache.size());
        lruCache.get("a");
        System.out.println("get a " + lruCache.prettyPrint() + ", size:"+lruCache.size());
        lruCache.set("e", "e");
        System.out.println("set e " + lruCache.prettyPrint() + ", size:"+lruCache.size());
        lruCache.set("d", "d1");
        System.out.println("set d1 " + lruCache.prettyPrint() + ", size:"+lruCache.size());
        lruCache.delete("c");
        System.out.println("delete c " + lruCache.prettyPrint() + ", size:"+lruCache.size());
        lruCache.delete("e");
        System.out.println("delete e " + lruCache.prettyPrint() + ", size:"+lruCache.size());
    }

}

package code.algorithm.leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/20
 */
public class P460 {

    static class LFUCache {

        private static int ts;

        private Map<Integer, Node> map = new HashMap<>();

        private PriorityQueue<Node> queue;

        private int capacity;

        static class Node {

            int key;

            int val;

            int count;

            int timestamp;

            public Node(int key, int val) {
                this.key = key;
                this.val = val;
                this.count = 0;
                this.timestamp = ts++;
            }

            public void refresh(){
                timestamp = ts++;
                count++;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Node node = (Node) o;
                return key == node.key;
            }

        }

        public LFUCache(int capacity) {
            this.capacity = capacity;
            queue = new PriorityQueue<>((o1, o2) -> {
                if(o1.count == o2.count){
                    return o1.timestamp - o2.timestamp;
                }
                return o1.count - o2.count;
            });
        }

        public int get(int key) {
            if(capacity == 0){
                return -1;
            }
            Node node = map.get(key);
            if(node != null){
                refresh(node);
                return node.val;
            }
            return -1;
        }

        /**
         * 1、更新访问次数和最近访问时间
         * 2、更新在node中的位置
         * @param node
         */
        private void refresh(Node node){
            queue.remove(node);
            node.refresh();
            queue.add(node);
        }

        public void put(int key, int value) {
            if(capacity == 0){
                return;
            }
            Node node = map.get(key);
            //已存在
            if(node != null){
                node.val = value;
                refresh(node);
                return;
            }

            if(map.size() == capacity){
                removeLFU();
            }
            node = new Node(key, value);
            map.put(key, node);
            queue.add(node);
        }

        private void removeLFU(){
            Node node = queue.poll();
            map.remove(node.key);
        }

    }

    public static void main(String[] args) {
        //["LFUCache","put","put","put","put","get"]
        //[[2],[3,1],[2,1],[2,2],[4,4],[2]]
        LFUCache lfuCache = new LFUCache(2);
        lfuCache.put(3, 1);
        lfuCache.put(2, 1);
        lfuCache.put(2, 2);
        lfuCache.put(4, 4);
        System.out.println(lfuCache.get(2));
    }
}

package code.algorithm.leetcode;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/17
 */
public class P707 {

    static class MyLinkedList {

        private Node head;

        private int size;

        static class Node{

            public Node(int val, Node prev, Node next) {
                this.val = val;
                this.prev = prev;
                this.next = next;
            }

            public Node(int val, Node prev) {
                this(val, prev, null);
            }

            public Node(){
                this(0,null);
            }

            int val;

            Node prev;

            Node next;
        }

        /** Initialize your data structure here. */
        public MyLinkedList() {
            head = new Node();
            size = 0;
        }

        /** Get the value of the index-th node in the linked list. If the index is invalid, return -1. */
        public int get(int index) {
            if(index < 0 || index >= size){
                return -1;
            }
            Node node = head;
            for (int i = 0; i <= index; i++) {
                node = node.next;
            }
            return node.val;
        }

        /** Add a node of value val before the first element of the linked list. After the insertion, the new node will be the first node of the linked list. */
        public void addAtHead(int val) {
            Node old1st = head.next;
            Node node = new Node(val, head, old1st);
            head.next = node;
            if(old1st != null){
                old1st.prev = node;
            }
            size++;
        }

        /** Append a node of value val to the last element of the linked list. */
        public void addAtTail(int val) {
            Node node = head;
            while (node.next != null){
                node = node.next;
            }
            node.next = new Node(val, node);
            size++;
        }

        /** Add a node of value val before the index-th node in the linked list. If index equals to the length of linked list, the node will be appended to the end of linked list. If index is greater than the length, the node will not be inserted. */
        /**
         * addAtIndex(index,val)：在链表中的第 index 个节点之前添加值为 val  的节点。
         * 如果 index 等于链表的长度，则该节点将附加到链表的末尾。
         * 如果 index 大于链表长度，则不会插入节点。
         * 如果index小于0，则在头部插入节点。
         *
         * @param index
         * @param val
         */
        public void addAtIndex(int index, int val) {
            if(index > size){
                return;
            }else if(index == size){
                addAtTail(val);
            }else if(index < 0){
                addAtHead(val);
            }else{
                Node node = head;
                for (int i = 0; i < index; i++) {
                    node = node.next;
                }
                Node nextNode = node.next;
                Node newNode = new Node(val, node, nextNode);
                node.next = newNode;
                nextNode.prev = newNode;
                size++;
            }
        }

        /** Delete the index-th node in the linked list, if the index is valid. */
        public void deleteAtIndex(int index) {
            if(index >= size || index < 0){
                return;
            }
            Node node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            Node after = node.next.next;
            node.next = after;
            if(after != null){
                after.prev = node;
            }
            size--;
        }

        public void print(){
            Node node = head;
            while (node.next != null){
                System.out.print("->"+node.next.val);
                node = node.next;
            }
            System.out.println();
        }

    }

    public static void main(String[] args) {
        MyLinkedList linkedList = new MyLinkedList();
        linkedList.addAtHead(9);
        linkedList.get(1);
        linkedList.addAtIndex(1,1);
        linkedList.addAtIndex(1,7);
        linkedList.deleteAtIndex(1);
        linkedList.addAtHead(7);
        linkedList.addAtHead(4);
        linkedList.deleteAtIndex(1);
        linkedList.addAtIndex(1,4);
        linkedList.addAtHead(2);
        linkedList.deleteAtIndex(5);
    }
}

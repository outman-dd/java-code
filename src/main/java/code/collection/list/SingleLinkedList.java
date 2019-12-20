package code.collection.list;

/**
 * 〈单链表〉<p>
 * 〈功能详细描述〉
 * -> [HEAD,next] -> [1,next] -> [2,nil]
 *
 * @author zixiao
 * @date 2019/11/22
 */
public class SingleLinkedList<E> implements IList<E> {

    private int size;

    private Node<E> head;

    private static class Node<E> {
        E item;
        Node<E> next;

        Node(E element, Node<E> next) {
            this.item = element;
            this.next = next;
        }
    }

    public SingleLinkedList() {
        head = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int indexOf(E o) {
        if (o == null) {
            throw new NullPointerException();
        }
        Node<E> node = head;
        int i = 0;
        while (node != null) {
            if (o.equals(node.item)) {
                return i;
            }
            i++;
            node = node.next;
        }
        return -1;
    }

    @Override
    public E get(int index) {
        checkIndex(index);

        Node<E> node = getNode(index);

        return node == null ? null : node.item;
    }

    private Node<E> getNode(int index) {
        Node<E> node = head;
        for (int i = 1; i <= index; i++) {
            if (node == null) {
                break;
            }
            node = node.next;
        }
        return node;
    }

    private void checkIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index can not be < 0");
        } else if (index >= size) {
            throw new IllegalArgumentException("bound out of list, index:" + index);
        }
    }

    @Override
    public void add(E e) {
        Node node = new Node(e, null);
        if (head == null) {
            head = node;
        } else {
            Node tail = findTail();
            tail.next = node;
        }
        size++;
    }

    private Node<E> findTail() {
        Node<E> node = head;
        while (node.next != null) {
            node = node.next;
        }
        return node;
    }

    @Override
    public void add(int index, E e) {
        if (index == 0) {
            Node<E> node = new Node<>(e, head);
            head = node;
        } else {
            checkIndex(index - 1);
            Node prev = getNode(index - 1);
            Node<E> node = new Node<>(e, prev.next);
            prev.next = node;
        }
        size++;
    }

    @Override
    public boolean remove(E e) {
        int idx = indexOf(e);
        if (idx == -1) {
            return false;
        }

        removeNode(idx);
        return true;
    }

    private Node<E> removeNode(int index){
        Node<E> toDelete = null;
        if (index == 0) {
            toDelete = head;
            head = head.next;
        } else {
            Node prev = getNode(index - 1);
            toDelete = prev.next;
            prev.next = prev.next.next;
        }
        toDelete.next = null;
        size--;
        return toDelete;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);

        return removeNode(index).item;
    }

    /**
     * 反转
     * 1: -> A -> B -> C -> D -> NULL
     *       p    q   head
     * 2: 指针反转，p.next = null; q.next = p;
     *    节点往前移动1步，p = q; q = head; head = head.next
     *  <- A <- B -> C -> D -> NULL
     *          p    q   head
     * 3: 指针反转, 节点往前移动1步
     *  <- A <- B <- C -> D -> NULL
     *               p    q   head
     * 4: head == null, 跳出循环
     *    q.next = p; head = q
     *  <- A <- B <- C <- D <-
     *               p   q(head)
     */
    public void reverse(){
        if(head == null || head.next == null){
            return;
        }
        Node<E> p = head;
        Node<E> q = p.next;
        head = q.next;
        p.next = null;
        while (head != null){
            q.next = p;
            p = q;
            q = head;
            head = head.next;
        }
        q.next = p;
        head = q;
        p = q = null;
    }

    public boolean isCycle(){
        if (head == null) {
            return false;
        }
        Node<E> p = head;
        Node<E> q = head;
        while (true) {
            p = p.next;
            if (q.next != null || q.next.next != null) {
                break;
            } else {
                q = q.next.next;
            }
            if(p == q && p != null){
                return true;
            }
        }
        return p == q && p != null;
    }

    @Override
    public String toString() {
        if (head == null) {
            return "->NULL";
        }
        StringBuilder sb = new StringBuilder();
        Node<E> node = head;
        while (node != null) {
            sb.append("->").append(node.item);
            node = node.next;
        }
        return sb.toString();
    }

}

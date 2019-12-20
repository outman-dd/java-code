package code.collection.queue;

/**
 * 〈无界链式队列〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/27
 */
public class ListQueue<E> implements IQueue<E> {

    private int size;

    private Node<E> head;

    private Node<E> tail;

    private static class Node<E> {
        E item;
        Node<E> next;

        Node(E element, Node<E> next) {
            this.item = element;
            this.next = next;
        }
    }

    public ListQueue() {
        head = tail = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean enqueue(E e) {
        Node<E> preTail = tail;
        tail = new Node<E>(e, null);
        if (preTail != null) {
            preTail.next = tail;
        } else {
            //队列为空
            head = tail;
        }
        size++;
        return true;
    }

    @Override
    public E dequeue() {
        if (size == 0) {
            System.out.println("Queue is empty");
            return null;
        }

        Node<E> node = head;
        if (head.next == null) {
            head = tail = null;
        } else {
            //队列只有一个元素，head=tail的情况
            head = head.next;
        }
        size--;
        return node.item;
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

    public static void main(String[] args) {
        ListQueue<String> listQueue = new ListQueue<>();
        System.out.println("Dequeue:" + listQueue.dequeue());
        System.out.println("Enqueue A:" + listQueue.enqueue("A"));
        System.out.println("Enqueue B:" + listQueue.enqueue("B"));
        System.out.print("Size:" + listQueue.size + ", "+listQueue);

        System.out.println("Dequeue :" + listQueue.dequeue());
        System.out.println("Dequeue :" + listQueue.dequeue());
        System.out.println("Dequeue :" + listQueue.dequeue());
        System.out.println("Size:" + listQueue.size + ", "+listQueue);

        System.out.println("Enqueue C:" + listQueue.enqueue("C"));
        System.out.println("Size:" + listQueue.size + ", "+listQueue);

        System.out.println("Dequeue :" + listQueue.dequeue());
        System.out.println("Size:" + listQueue.size + ", "+listQueue);
    }
}

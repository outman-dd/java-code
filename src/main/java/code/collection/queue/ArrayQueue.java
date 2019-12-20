package code.collection.queue;

/**
 * 〈有界顺序队列〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public class ArrayQueue<E> implements IQueue<E> {

    protected Object[] elements;

    protected int capacity;

    protected int head;

    protected int tail;

    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        elements = new Object[capacity + 1];
        head = tail = 0;
    }

    @Override
    public int size() {
        return tail - head;
    }

    @Override
    public boolean enqueue(E e) {
        if (tail == capacity) {
            if (head == 0) {
                System.out.println("Queue is full");
                return false;
            }
            move();
        }
        elements[tail++] = e;
        return true;
    }

    private void move(){
        Object[] newElements = new Object[capacity];
        int j = 0;
        for (int i = head; i < tail; i++, j++) {
            newElements[j] = elements[i];
        }
        elements = null;
        elements = newElements;
        head = 0;
        tail = j;
    }

    @Override
    public E dequeue() {
        if (head == tail) {
            System.out.println("Queue is empty");
            return null;
        }
        E e = (E)elements[head];
        elements[head] = null;
        head++;
        return e;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = head; i < tail; i++) {
            sb.append(elements[i]).append("-");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ArrayQueue<String> arrayQueue = new ArrayQueue<>(4);
        System.out.println("Queue size: " + arrayQueue.size());
        System.out.println(arrayQueue.dequeue());

        arrayQueue.enqueue("A");
        arrayQueue.enqueue("B");
        arrayQueue.enqueue("C");
        arrayQueue.enqueue("D");

        System.out.println(arrayQueue.dequeue());
        System.out.println(arrayQueue.dequeue());
        System.out.println(arrayQueue.dequeue());
        arrayQueue.enqueue("E");
        arrayQueue.enqueue("F");

        System.out.println("Queue size: " + arrayQueue.size());
        System.out.println(arrayQueue);
    }
}

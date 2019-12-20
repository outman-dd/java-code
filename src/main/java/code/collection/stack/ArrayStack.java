package code.collection.stack;

/**
 * 〈数组栈〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public class ArrayStack<E> implements IStack<E> {

    protected Object[] elements;

    protected int index = -1;

    protected int capacity;

    public ArrayStack(int capacity) {
        this.capacity = capacity;
        elements = new Object[this.capacity];
    }

    @Override
    public int size() {
        return index+1;
    }

    @Override
    public void push(E e) {
        if(size() >= capacity){
            throw new IllegalArgumentException("Stack is full");
        }
        elements[++index] = e;
    }

    @Override
    public E pop() {
        if(size() == 0){
            return null;
        }
        return (E)elements[index--];
    }

    @Override
    public void clear() {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
        index = -1;
    }

    public static void main(String[] args) {
        ArrayStack<Integer> stack = new ArrayStack<>(3);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        try {
            stack.push(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
    }
}

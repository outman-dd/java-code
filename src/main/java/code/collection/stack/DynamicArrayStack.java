package code.collection.stack;

import java.util.Arrays;

/**
 * 〈动态扩容的数组栈〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public class DynamicArrayStack<E> extends ArrayStack<E> {

    protected int initialCapacity;

    public DynamicArrayStack(int initialCapacity) {
        super(initialCapacity);
        this.initialCapacity = initialCapacity;
    }

    @Override
    public void push(E e) {
        if(size() >= capacity){
            resize();
        }
        elements[++index] = e;
    }

    private void resize(){
        capacity = capacity<<1;
        Object[] oldArray = elements;
        elements = Arrays.copyOf(oldArray, capacity);
        oldArray = null;
    }

    @Override
    public void clear(){
        super.clear();
        elements = new Object[initialCapacity];
    }

    public static void main(String[] args) {
        DynamicArrayStack<Integer> stack = new DynamicArrayStack<>(2);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);

        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
    }
}

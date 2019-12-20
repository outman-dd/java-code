package code.collection.list;

import org.junit.Test;
import org.springframework.util.Assert;

/**
 * 〈单链表〉<p>
 * 〈功能详细描述〉
 *  -> [HEAD,next] -> [1,next] -> [2,nil]
 * @author zixiao
 * @date 2019/11/22
 */
public class SingleLinkedListTest{

    @Test
    public void testIndexOf(){
        SingleLinkedList<String> linkedList = new SingleLinkedList<String>();
        Assert.isTrue(linkedList.indexOf("a") == -1, "");

        linkedList.add("a");

        Assert.isTrue(linkedList.indexOf("a") == 0, "");
        Assert.isTrue(linkedList.indexOf("b") == -1, "");

        linkedList.add("b");

        Assert.isTrue(linkedList.indexOf("a") == 0, "");
        Assert.isTrue(linkedList.indexOf("b") == 1, "");
        Assert.isTrue(linkedList.indexOf("c") == -1, "");
    }

    @Test
    public void testGet(){
        SingleLinkedList<String> linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.add("b");

        System.out.println("index 0: "+linkedList.get(0));
        System.out.println("index 1: "+linkedList.get(1));
        try {
            System.out.println("index 2: "+linkedList.get(2));
        } catch (Exception e) {
            Assert.isTrue(e instanceof IllegalArgumentException, "");
        }
    }

    @Test
    public void testAdd() {
        SingleLinkedList<String> linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.add("b");

        Assert.isTrue(linkedList.size() == 2, "");
        System.out.println(linkedList);
    }

    @Test
    public void testAdd2() {
        SingleLinkedList<String> linkedList = new SingleLinkedList<String>();
        linkedList.add(0, "a");
        linkedList.add(1, "b");
        linkedList.add(0, "a0");
        linkedList.add(1, "a1");

        Assert.isTrue(linkedList.size() == 4, "");
        System.out.println(linkedList);
    }

    @Test
    public void testDelete(){
        SingleLinkedList<String> linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.add("b");
        linkedList.add("c");

        linkedList.remove("a");
        Assert.isTrue(linkedList.size() == 2, "");
        System.out.println(linkedList);

        linkedList.remove("c");
        Assert.isTrue(linkedList.size() == 1, "");
        System.out.println(linkedList);

        linkedList.remove("b");
        Assert.isTrue(linkedList.size() == 0, "");
        System.out.println(linkedList);
    }

    @Test
    public void testDelete2(){
        SingleLinkedList<String> linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.add("b");
        linkedList.add("c");

        String deleted = linkedList.remove(0);
        Assert.isTrue("a".equals(deleted), "");
        Assert.isTrue(linkedList.size() == 2, "");
        System.out.println(linkedList);

        deleted = linkedList.remove(1);
        Assert.isTrue("c".equals(deleted), "");
        Assert.isTrue(linkedList.size() == 1, "");
        System.out.println(linkedList);

        deleted = linkedList.remove(0);
        Assert.isTrue("b".equals(deleted), "");
        Assert.isTrue(linkedList.size() == 0, "");
        System.out.println(linkedList);
    }

    @Test
    public void testReverse(){
        SingleLinkedList<String> linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.reverse();
        System.out.println(linkedList);

        linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.add("b");
        linkedList.reverse();
        System.out.println(linkedList);

        linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.add("b");
        linkedList.add("c");
        linkedList.reverse();
        System.out.println(linkedList);

        linkedList = new SingleLinkedList<String>();
        linkedList.add("a");
        linkedList.add("b");
        linkedList.add("c");
        linkedList.add("d");
        linkedList.reverse();
        System.out.println(linkedList);
    }

}

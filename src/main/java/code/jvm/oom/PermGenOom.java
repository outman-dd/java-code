package code.jvm.oom;

import javassist.ClassPool;

/**
 * 〈方法区溢出〉<p>
 *   -Xms512m -Xmx512m -Xmn320m -XX:PermSize=8m -XX:MaxPermSize=8m
 *   -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/zixiao/dump/
 *
 * @author zixiao
 * @date 18/3/2
 */
public class PermGenOom {

    public static void main(String[] args) throws Exception {
        //1、加载大量Class
        oomByClass();

        //2、常量池加入运行时常量
        //oomByConstants();
    }

    public static void oomByClass() throws Exception {
        for (int i = 0; i <100000; i++) {
            generate("code.jvm.oom.User" + i);
        }
    }

    private static Class generate(String name) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        return pool.makeClass(name).toClass();
    }

    /**
     * 必须使用JDK 1.6
     * String的intern() 会在常量池中找是否存在相同字符串值，没有拷贝一个到常量池中
     *
     * ﻿连接表达式 +
     （1）只有使用引号包含文本的方式创建的String对象之间使用“+”连接产生的新对象才会被加入字符串池中。
      "aaa"+"bbb"的字符串进入常量池
     （2）对于所有包含字符串对象（包括null）的“+”连接表达式，它所产生的新对象都不会被加入字符串池中。
      str +"bbb"的字符串不会进入常量池
     */
    public static void oomByConstants(){
        String prefix = "1234567891234567890123456789123456789012345678912345678901234567891234567890123456789123456789012345678912345678901234567891234567890123456789123456789012345678912345678901234567891234567890";
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            //String s1 = "1234567891234567890"+i;
            prefix = String.valueOf(prefix + i).intern();
            System.out.println(i);
        }
    }
}

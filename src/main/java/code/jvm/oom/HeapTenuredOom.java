package code.jvm.oom;

/**
 * 〈堆内存溢出〉<p>
 * 〈功能详细描述〉
 *
 * JVM参数： -Xms32m -Xmx32m -Xmn16m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/zixiao/dump/
 *
 * 开辟数组 int[4*1024*1024]，约占16+m空间
 *
 * 1、-Xms32m -Xmx32m -Xmn21m  不溢出
 * 原因：Eden区16.8m，﻿Tenured区11m， Eden可以存放16+m数据
 *
 * 2、-Xms32m -Xmx32m -Xmn16m  溢出
 * 原因：Eden区12.8m，﻿Tenured区16mb， Eden和Tenured区都不够存放16+m数据
 *
 * 3、-Xms32m -Xmx32m -Xmn15m  不溢出
 * 原因：Eden区12m，﻿Tenured区17m， Eden不够存放16+m数据，直接放入﻿Tenured区
 *
 *
 * @author zixiao
 * @date 18/3/2
 */
public class HeapTenuredOom {

    public static void main(String[] args) throws InterruptedException {
        //int[4*1024*1024] 约占16+mb空间
        int[] array = new int[4*1024*1024];
        System.out.println("success");
    }
}

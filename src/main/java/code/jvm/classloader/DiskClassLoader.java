package code.jvm.classloader;

import java.io.*;
import java.lang.reflect.Method;

/**
 * 磁盘类加载器
 *
 * 类加载：双亲委托模式
 * Bootstrap
 *    |
 *   Ext
 *    |
 *   App
 *    |
 *  自定义
 *
 */
public class DiskClassLoader extends ClassLoader {

    /**
     * 类文件根路径
     */
    protected String classRoot;

    public DiskClassLoader(String classRoot) {
        this.classRoot = classRoot;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        String filePath = getFileName(className);
        File file = new File(classRoot, filePath);
        FileInputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] data;
            int len;
            while ((len = is.read()) != -1) {
                bos.write(len);
            }
            data = bos.toByteArray();
            Class<?> clazz = defineClass(className, data, 0, data.length);
            return clazz;
        } catch (IOException e) {
            throw new ClassNotFoundException(className + ", error:"+e.getMessage());
        } catch(ClassFormatError e){
            throw new ClassNotFoundException(className + ", error:"+e.getMessage());
        } finally {
            closeQuietly(is);
            closeQuietly(bos);
        }
    }

    private void closeQuietly(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String getFileName(String name) {
        return name.replaceAll("\\.", File.separator) +".class";
    }

    public static void main(String[] args) throws ClassNotFoundException {
        DiskClassLoader diskLoader = new DiskClassLoader("/Users/zixiao");

        //Bootstrap
        String className = "java.lang.String";
        Class clazz = diskLoader.loadClass(className);
        System.out.println(className + " => " + clazz.getClassLoader());

        //ExtClassLoader
        className = "com.sun.crypto.provider.AESCipher";
        clazz = diskLoader.loadClass(className);
        System.out.println(className + " => " + clazz.getClassLoader());

        //AppClassLoader
        className = "code.jvm.classloader.DiskClassLoader";
        clazz = diskLoader.loadClass(className);
        System.out.println(className + " => " + clazz.getClassLoader());

        //DiskClassLoader
        className = "com.Hello";
        clazz = diskLoader.loadClass(className);
        System.out.println(className + " => " + clazz.getClassLoader());
        try {
            Object obj = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("sayHello",null);
            method.invoke(obj, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

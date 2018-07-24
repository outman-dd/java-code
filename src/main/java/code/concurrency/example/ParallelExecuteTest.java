package code.concurrency.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 〈并行执行〉<p>
 * 使用CompletionService 提高多个任务的处理性能
 * 模拟电商中加载商品详情
 * 1、数据库查询基本信息 (快)
 * 2、下载多种图片 （每张图片下载较慢）
 * 3、调用外部系统接口 （慢）
 *
 * @author zixiao
 * @date 18/3/21
 */
public class ParallelExecuteTest {

    //线程池
    private final ExecutorService executorService;

    public ParallelExecuteTest() {
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void display(final String productId) {

        //1、数据库查询
        List<Info> infos = queryInfoFromDb(productId);

        CompletionService completionService = new ExecutorCompletionService(executorService);

        //2、每个图像的下载
        final List<String> urls = loadUrls(productId);
        for (final String url : urls) {
            completionService.submit(new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    return downloadImage(url);
                }
            });
        }

        //3、外部接口调用
        completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return callExternalInterface(productId);
            }
        });

        //4、获取异步调用结果
        int taskNum = urls.size() + 1;
        String callExternalInterfaceRet = null;
        List<byte[]> images = new ArrayList<byte[]>();
        try {
            //任意任务完成后就把其结果加到结果队列中，而不用依次等待每个任务完成
            for (int i = 0; i < taskNum; i++){
                Future<Object> future = completionService.take();
                Object object = future.get();
                if(object instanceof String){
                    callExternalInterfaceRet = (String) object;
                }else if(object instanceof byte[]){
                    images.add((byte[]) object);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            try {
                throw new Throwable(e.getCause());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        //显示数据
        System.out.println("-------------- 基本信息 ---------------");
        for(Info info : infos){
            System.out.println(info);
        }

        System.out.println("-------------- 接口调用 ---------------");
        System.out.println(callExternalInterfaceRet);

        System.out.println("-------------- 图片列表 ---------------");
        for(byte[] bytes : images){
            System.out.println(new String(bytes));
        }
    }

    private List<String> loadUrls(String productId){
        try {
            Thread.sleep(30L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        List<String> urls = new ArrayList<String>(8);
        String prefix = productId + "/";
        urls.add(prefix + "1.jpg");
        urls.add(prefix + "2.jpg");
        urls.add(prefix + "3.jpg");
        urls.add(prefix + "4.jpg");
        urls.add(prefix + "5.jpg");
        urls.add(prefix + "6.jpg");

        return urls;
    }

    /**
     * 查询详情列表
     * 耗时：50ms
     * @return
     */
    private List<Info> queryInfoFromDb(String param){
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        List<Info> infos = new ArrayList<Info>(4);
        infos.add(new Info("品牌", "Mazda"));
        infos.add(new Info("型号", "CX-5 2.5L"));
        infos.add(new Info("价格", "21.98万"));
        return infos;
    }

    /**
     * 查询外部接口
     * 耗时：1500ms
     * @return
     */
    private String callExternalInterface(String param){
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "OK";
    }


    /**
     * 下载图片
     * 耗时：800-1000ms
     * @param url
     * @return
     */
    private byte[] downloadImage(String url){
        try {
            Thread.sleep(new Random().nextInt(200)+800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ("Http://file.test.com/" +url).getBytes();
    }

    public void shutdown(){
        executorService.shutdown();
    }


    private static class Info implements Serializable{

        private String name;

        private String value;

        public Info(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + ":" + value;
        }
    }

    public static void main(String[] args) {
        ParallelExecuteTest showService = new ParallelExecuteTest();
        long start = System.currentTimeMillis();
        showService.display("123333");

        System.out.println("\n\rTotal costs: " + (System.currentTimeMillis() - start) +"ms");
        showService.shutdown();
    }

}


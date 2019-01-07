package code.concurrency.example.cat;

import code.util.DateFormatUtils;
import org.junit.Test;

import java.io.*;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈LogTest〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/12
 */
public class CatSortTest {

    private static String LINE = "%s IP:127.0.0.1 Login, URL:https://blog.csdn.net/u012375924/article/details/%d";

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static String filePath = "/Users/zixiao/logs/";

    @Test
    public void start() throws IOException, InterruptedException {
        //准备数据
        //generateLogs(100, 10000);

        //catAndSort(filePath + "*.log", "Login", SortType.DESC);

        catAndSort2(filePath + "*.log", "Login", SortType.DESC);
    }

    private void catAndSort(String filePath, String keyWord, SortType sortType ){
        ICommand command = new CatSortCommand(filePath, keyWord, sortType);
        Result<CatSortResult> resultResult = command.execute();
        if(!resultResult.isSuccess()){
            System.err.println(resultResult.getErrorMsg());
        }else{
            int total = 0;
            for(CatSortResult catSortResult : resultResult.getResultList()){
                total += catSortResult.getCount();
            }
            System.out.println("Total:"+total);
        }
    }

    private void catAndSort2(String filePath, String keyWord, SortType sortType ){
        ICommand command = new CatSortCommand2(filePath, keyWord, sortType);
        Result<CatSortResult> resultResult = command.execute();
        if(!resultResult.isSuccess()){
            System.err.println(resultResult.getErrorMsg());
        }else{
            int total = 0;
            for(CatSortResult catSortResult : resultResult.getResultList()){
                total += catSortResult.getCount();
            }
            System.out.println("Total:"+total);
        }
    }

    private void generateLogs(int fileNum, int lineNum) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(fileNum);
        for(int i=1; i<=fileNum; i++){
            executorService.submit(new Task(i, lineNum, latch));
        }
        latch.await();
        executorService.shutdown();
    }

    static class Task implements Runnable {

        private int fileNo;

        private int lineNum;

        private CountDownLatch latch;

        public Task(int fileNo, int lineNum, CountDownLatch latch) {
            this.fileNo = fileNo;
            this.lineNum = lineNum;
            this.latch = latch;
        }

        public void run() {
            try {
                Random random = new Random();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + fileNo + ".log"), "GBK"));
                for (int j = 0; j < lineNum; j++) {
                    if (j > 0) {
                        out.newLine();
                    }
                    out.write(String.format(LINE, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"), random.nextInt(4)));
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }

}

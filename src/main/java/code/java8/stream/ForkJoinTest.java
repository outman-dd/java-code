package code.java8.stream;

import org.springframework.util.StopWatch;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
public class ForkJoinTest {

    private static final int THRESHOLD = 10;

    private static final int NUM = 100000;

    private static final int HANDLE_TIME = 1;

    private static final ForkJoinMode MODE = ForkJoinMode.INVOKEALL;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        StopWatch stopWatch = new StopWatch("test");

        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        for(int i=0; i<10; i++){
            stopWatch.start(MODE.name());
            Future<Long> result = forkJoinPool.submit(new RTask(0, NUM-1));
            System.out.println("forkjoin result:" + result.get());
            stopWatch.stop();
        }

        System.out.println(stopWatch.prettyPrint());
    }

    private static class RTask extends RecursiveTask<Long> {

        private int start;
        private int end;

        public RTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long sum = 0;
            if((end - start) < THRESHOLD){
                System.out.println(Thread.currentThread().getName() + " compute, start:" + start + ", end:" + end);
                for(int i = start; i<= end;i++){
                    sum += i;
                }
                wait1();
            }else{
                System.out.println(Thread.currentThread().getName()+" split, start:" + start + ", end:" + end);
                int middle = (start + end) /2;
                RTask left = new RTask(start, middle);
                RTask right = new RTask(middle + 1, end);
                if(MODE.equals(ForkJoinMode.FORK)){
                    left.fork();
                    right.fork();
                }else{
                    invokeAll(left, right);
                }
                sum = left.join() + right.join();
            }
            return sum;
        }
    }

    private static void wait1(){
        try {
            Thread.sleep(HANDLE_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}



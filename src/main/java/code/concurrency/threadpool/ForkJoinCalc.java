package code.concurrency.threadpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/11/5
 */
public class ForkJoinCalc extends RecursiveTask<Long> {

    private static final int THRESHOLD = 100;
    private int start;
    private int end;

    public ForkJoinCalc(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long sum = 0;
        if((end - start) < THRESHOLD){
            for(int i = start; i<= end;i++){
                sum += i;
            }
        }else{
            int middle = (start + end) /2;
            ForkJoinCalc left = new ForkJoinCalc(start, middle);
            ForkJoinCalc right = new ForkJoinCalc(middle + 1, end);
//            left.fork();
//            right.fork();
            invokeAll(left, right);

            sum = left.join() + right.join();
        }
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int maxValue = 1000000000;
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<Long> result = forkJoinPool.submit(new ForkJoinCalc(0, maxValue));
        System.out.println("ForkJoinCostTime:"+(System.currentTimeMillis()-start)+ "ms, result:"+result.get());

        start = System.currentTimeMillis();
        long sum = 0;
        for(int i=0;i<=maxValue;i++){
            sum+=i;
        }
        System.out.println("CostTime:"+(System.currentTimeMillis()-start) + "ms, result:"+sum);
        forkJoinPool.shutdown();

    }
}
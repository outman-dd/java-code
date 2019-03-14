package code.concurrency.example.printer;

import java.util.concurrent.locks.LockSupport;

/**
 * 〈多线程打印 LockSupport〉<p>
 * 〈有三个线程分别打印A、B、C，请用多线程编程实现，在屏幕打印10次ABC能详细描述〉
 *
 * @author zixiao
 * @date 18/7/23
 */
public class MutilThreadPrinter2 {

    private static int PRINT_TIMES = 1000000;

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        PrinterThread threadA = new PrinterThread();
        PrinterThread threadB = new PrinterThread();
        PrinterThread threadC = new PrinterThread();

        threadA.startPrint("A", threadB);
        threadB.startPrint("B", threadC);
        threadC.startPrint("C", threadA);
        LockSupport.unpark(threadA);
        threadC.join();
        System.out.println("\n\rCost:" + (System.currentTimeMillis() - start));
    }

    private static void print(String s){
        System.out.print(s);
    }

    static class PrinterThread extends Thread {

        private String printChar;

        private PrinterThread nextPrinter;

        public void startPrint(String printChar, PrinterThread nextPrinter){
            this.printChar = printChar;
            this.nextPrinter = nextPrinter;
            this.start();
        }

        @Override
        public void run() {
            for (int i = 0; i < PRINT_TIMES; i++) {
                LockSupport.park();
                print(printChar);
                LockSupport.unpark(nextPrinter);
            }
        }
    }

}

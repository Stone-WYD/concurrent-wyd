package interview;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @program: concurrent-wyd
 * @description: 两个线程轮流打印
 * @author: Stone
 * @create: 2023-10-11 16:49
 **/
public class PrintInTurn {
    public static void main(String[] args) throws InterruptedException {
        test1();
    }

    public static void test1() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition c1 = lock.newCondition();
        Condition c2 = lock.newCondition();

        Thread t1 = new Thread(() -> {
            int i = 1;
            while (true){
                lock.lock();
                try {
                    if (i != 1){
                        c2.signal();
                    }
                    c1.await();
                    print();
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    print();
                    c1.signal();

                    c2.await();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }, "t2");
        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    public static void print(){
        if (Thread.currentThread().getName().equals("t1")) {
            System.out.println("t1 print a");
        } else System.out.println("t2 print b");
    }
}




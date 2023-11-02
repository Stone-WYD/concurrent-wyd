package interview;

import wyd.test.util.Sleeper;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: concurrent-wyd
 * @description:
 * @author: Stone
 * @create: 2023-11-02 10:36
 **/
public class TestLock {

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition c1 = lock.newCondition();
        Thread thread = new Thread(() -> {
            lock.lock();
            System.out.println("t1：获取到了锁");
            System.out.println("t1：挂在了 c1 上");
            try {
                c1.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("t1：从 c1 上下来了");
        }, "t1");

        Thread t2 = new Thread(() -> {
            System.out.println("t2：睡眠 2 s");
            Sleeper.sleep(2);
            System.out.println("t2：唤醒在 c1 上的 t1");
            lock.lock();
            c1.signal();
            lock.unlock();
            System.out.println("2t：睡眠 2 s");
            Sleeper.sleep(2);
        }, "t2");

        thread.start();
        t2.start();

        thread.join();
        t2.join();
    }
}

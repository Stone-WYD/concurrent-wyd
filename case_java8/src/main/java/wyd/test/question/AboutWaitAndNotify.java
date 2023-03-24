package wyd.test.question;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c")
public class AboutWaitAndNotify {

    final static ReentrantLock lock = new ReentrantLock();
    final static Condition a = lock.newCondition();
    final static Condition b = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            lock.lock();
            try {
                try {
                    a.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("t1 print: 1");
                b.signal();
                log.debug("signal t2...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                lock.unlock();
            }

        }, "t1").start();

        new Thread(()->{
            lock.lock();
            try {
                try {
                    b.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("t2 print 2");
            } finally {
                lock.unlock();
            }

        }, "t2").start();

        Thread.sleep(1000);
        lock.lock();
        log.debug("signal t1...");
        a.signal();
        lock.unlock();

    }
}

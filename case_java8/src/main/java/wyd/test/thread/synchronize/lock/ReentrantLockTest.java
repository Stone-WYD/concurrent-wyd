package wyd.test.thread.synchronize.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c")
public class ReentrantLockTest {
    private static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) {
        testTryLock();
    }

    public static void testReentry(){
        lock.lock();
        try {
            log.debug("test reentry...");
            r1();
        }finally {
            lock.unlock();
        }
    }
    public static void r1(){
        lock.lock();
        try {
            log.debug("entry m1...");
        }finally {
            lock.unlock();
        }
    }

    public static void testInterrupt(){
        Thread t1 = new Thread(() -> {
            try {
                log.debug("尝试获得锁...");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                log.debug("因被打断而没有获得锁...");
                e.printStackTrace();
            }
        }, "t1");
        lock.lock();
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t1.interrupt();
        lock.unlock();

    }

    public static void testTryLock(){
        Thread t1 = new Thread(() -> {
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.debug("没有获得锁");
                    return;
                }
            }catch (InterruptedException e){
                e.printStackTrace();
                log.debug("没有获得锁");
                return;
            }

            try {
                log.debug("获得到了锁");
            }finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }
}

package wyd.test.thread.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c")
public class FixedOrder {
    static Object lock = new Object();
    static boolean t2runned = false;
    static ReentrantLock reentrantLock = new ReentrantLock();
    static Condition fixedOrderFlag = reentrantLock.newCondition();

    public static void main(String[] args) {
        // 固定顺序，t2一定比t1先打印
        useParkUnpark();
    }

    static public void useWaitNotify(){
        Thread t1 = new Thread(() -> {
            synchronized (lock){
                while(!t2runned){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                // t2运行完了
                log.debug("1");
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lock){
                log.debug("2");
                t2runned = true;
                lock.notifyAll();
            }
        }, "t2");

        t1.start();
        t2.start();
    }

    static public void useAwaitSingal(){

        Thread t1 = new Thread(() -> {
            reentrantLock.lock();
            try{
                while (!t2runned) {
                    try {
                        fixedOrderFlag.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("1");
            }finally {
                reentrantLock.unlock();
            }
        }, "t1");

        Thread t2 = new Thread(()->{
            reentrantLock.lock();
            try{
                log.debug("2");
                t2runned = true;
                fixedOrderFlag.signalAll();
            }finally {
                reentrantLock.unlock();
            }
        }, "t2");

        t1.start();
        t2.start();
    }

    static public void useParkUnpark(){
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("1");
        }, "t1");

        Thread t2 = new Thread(() -> {
            log.debug("2");
            LockSupport.unpark(t1);
        }, "t2");

        t1.start();
        t2.start();


    }
}

package wyd.test.thread.synchronize.waitnotify;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c")
public class Test2 {

    // wait 和 sleep的区别

    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        new Thread(()->{
            log.debug("获得锁...");
            synchronized (lock){
                try {
                    lock.wait(20000);
                    // Thread.sleep(20000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();


        TimeUnit.SECONDS.sleep(1);
        synchronized(lock){
            log.debug("获得锁...");
        }
    }
}

package wyd.test.thread.synchronize.waitnotify;

import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "c")
public class Test1 {

    static final Object obj = new Object();
    public static void main(String[] args) throws InterruptedException {


        Thread t1 = new Thread(() -> {
            log.debug("执行其他代码...");
            synchronized (obj){
                log.debug("进入waitSet队列等待");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("继续执行...");
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            log.debug("执行其他代码...");
            synchronized (obj){
                log.debug("进入waitSet队列等待");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("继续执行...");
        }, "t2");
        t2.start();

        Thread.sleep(1000);
        log.debug("唤醒线程");
        synchronized (obj){
            // obj.notify();
            obj.notifyAll();
        }
    }
}

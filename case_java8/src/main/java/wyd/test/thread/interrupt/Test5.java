package wyd.test.thread.interrupt;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c")
public class Test5 {
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            while (true){
                if (Thread.currentThread().isInterrupted()) {
                    log.debug("t1被打断了");
                    break;
                }
            }
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.debug("打断t1线程");
        t1.interrupt();
    }
}

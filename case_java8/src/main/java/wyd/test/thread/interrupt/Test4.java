package wyd.test.thread.interrupt;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c")
public class Test4 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.debug("t1开始睡眠2s");
                TimeUnit.SECONDS.sleep(1);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"t1");
        t1.start();
        log.debug("interrupt");
        t1.interrupt();
        log.debug("t1打断标记为：{}", t1.isInterrupted());
        TimeUnit.SECONDS.sleep(3);
        log.debug("3s后t1打断标记为：{}", t1.isInterrupted());
    }
}

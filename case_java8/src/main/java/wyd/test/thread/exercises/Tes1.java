package wyd.test.thread.exercises;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.Test7")
public class Tes1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                log.debug("洗完茶壶");

                TimeUnit.SECONDS.sleep(1);
                log.debug("洗完茶杯");

                TimeUnit.SECONDS.sleep(2);
                log.debug("拿茶叶来了");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "小王");
        t1.start();

        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
                log.debug("洗完水壶了");
                TimeUnit.SECONDS.sleep(8);
                log.debug("水烧好了");
                t1.join();
                log.debug("开始泡茶");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"老王").start();
    }

}

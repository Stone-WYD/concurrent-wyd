package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j(topic = "c")
public class TestShutDown {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> future1 = pool.submit(() -> {
            log.debug("1begin");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1end");
            return "1";
        });
        pool.submit(() -> {
            log.debug("2begin");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("2end");
            return "2";
        });
        pool.submit(() -> {
            log.debug("3begin");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("3end");
            return "3";
        });
        List<Runnable> runnables = pool.shutdownNow();
        log.debug("other...{}", runnables);



    }
}

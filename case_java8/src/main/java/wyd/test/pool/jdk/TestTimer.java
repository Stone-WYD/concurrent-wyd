package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static wyd.test.util.Sleeper.sleep;

/**
 * @author Stone
 */
@Slf4j(topic = "c")
public class TestTimer {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor pool =
                new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
                    private final AtomicInteger num = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "myThread-" + num.incrementAndGet());
                    }
                });
        // timePoolTest(pool);
        // scheduleAtFixedRate(pool);

        pool.scheduleWithFixedDelay( () -> {
            log.debug("running...");
            sleep(2);
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void scheduleAtFixedRate(ScheduledThreadPoolExecutor pool) {
        pool.scheduleAtFixedRate( () ->{
            log.debug("running...");
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void timePoolTest(ScheduledThreadPoolExecutor pool) {
        pool.schedule(()->{
            log.debug("task1");
            sleep(2);
        }, 1, TimeUnit.SECONDS);

        pool.schedule(() -> log.debug("task2")
                , 1, TimeUnit.SECONDS);
    }
}

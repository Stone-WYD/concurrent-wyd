package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Stone
 */
@Slf4j(topic = "c")
public class TestException {
    public static void main(String[] args) {

        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "myThread-" + num.incrementAndGet());
            }
        });

        pool.submit(()->{int i = 1/0;});

    }

    private static void testExceptionWithThread() {
        new Thread(() -> {
           int i = 1/0;
        }).start();
    }
}

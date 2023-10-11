package wyd.test.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class TestCyclicBarrier {
    public static void main(String[] args) {
        ExecutorService pool = new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, ()->{ log.debug("task1, task2 finish..."); });

        // 使用for循环的话，线程数需要和计数器的计数值相等
        for (int i = 0; i < 2; i++) {
            pool.submit(()->{
                log.debug("task1 begin...");
                sleep(1);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });

            pool.submit(()->{
                log.debug("task2 begin...");
                sleep(2);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        pool.shutdown();
    }
}

package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static wyd.test.util.Sleeper.sleep;

/**
 * @author Stone
 */
@Slf4j(topic = "c")
public class TestSubmit {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testSubmit();
    }

    private static void testInvokeAny() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        String s = pool.invokeAny(Arrays.asList(
                () -> {
                    log.debug("begin");
                    Thread.sleep(1000);
                    log.debug("end");
                    return "1";
                } ,
                () -> {
                    log.debug("begin");
                    Thread.sleep(2000);
                    log.debug("end");
                    return "3";
                }
        ));

        log.debug("result: {}", s);
    }

    private static void testInvokeAll() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    log.debug("begin");
                    Thread.sleep(1000);
                    return "1";
                } ,
                () -> {
                    log.debug("begin");
                    Thread.sleep(500);
                    return "2";
                },
                () -> {
                    log.debug("begin");
                    Thread.sleep(2000);
                    return "3";
                }
        ));
        futures.forEach( f->{
            try {
                log.debug("{}", f.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void testSubmit() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> future = pool.submit(() -> {
            log.debug("running...");
            sleep(1);
            return "ok";
        });

        log.debug("result : {}", future.get());
    }
}

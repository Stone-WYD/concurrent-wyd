package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class TestSubmit {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> future = pool.submit(() -> {
            log.debug("running...");
            sleep(1);
            return "ok";
        });

        log.debug("result : {}", future.get());
    }
}

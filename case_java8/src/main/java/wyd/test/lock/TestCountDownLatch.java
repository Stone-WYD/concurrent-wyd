package wyd.test.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c")
public class TestCountDownLatch {
    public static void main(String[] args) throws InterruptedException {
        rest();
    }

    private static void rest() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        log.debug("begin");
        ExecutorService service = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(4);

        service.submit(()->{
            try {
                Map<String, Object> response = restTemplate.getForObject("http://localhost:8080/order/{1}", Map.class, 1);
                log.debug("end order: {}", response);
            } catch (Exception e){
                log.debug("error: {}", e.getMessage());
            }
            latch.countDown();
        });

        service.submit(()->{
            try {
                Map<String, Object> response = restTemplate.getForObject("http://localhost:8080/product/{1}", Map.class, 1);
                log.debug("end product1: {}", response);
            } catch (Exception e){
                log.debug("error: {}", e.getMessage());
            }
            latch.countDown();
        });

        service.submit(()->{
            try {
                Map<String, Object> response = restTemplate.getForObject("http://localhost:8080/product/{2}", Map.class, 2);
                log.debug("end product2: {}", response);
            } catch (Exception e){
                log.debug("error: {}", e.getMessage());
            }
            latch.countDown();
        });

        service.submit(()->{
            try {
                Map<String, Object> response = restTemplate.getForObject("http://localhost:8080/logistics/{1}", Map.class, 1);
                log.debug("end logisticws: {}", response);
            } catch (Exception e){
                log.debug("error: {}", e.getMessage());
            }
            latch.countDown();
        });
        latch.await();
        log.debug("执行完毕");
        service.shutdown();
    }

    private static void gameStart() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        String[] all = new String[10];
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int k = i;
            pool.submit(()->{
                for (int j = 0; j <= 100; j++) {
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    all[k] = j + "%";
                    System.out.print( "\r" + Arrays.toString(all));
                }
                latch.countDown();
            });
        }
        latch.await();
        System.out.println("\n游戏开始！");
        pool.shutdown();
    }
}

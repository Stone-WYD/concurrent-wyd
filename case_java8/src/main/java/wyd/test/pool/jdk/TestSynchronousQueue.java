package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class TestSynchronousQueue {
    public static void main(String[] args) {
        SynchronousQueue<Integer> integers = new SynchronousQueue<>();
        new Thread(()->{
            try{
                log.debug("putting {}", 1);
                integers.put(1);
                log.debug("{} putted...", 1);

                log.debug("putting...{}", 2);
                integers.put(2);
                log.debug("{} putted...", 2);

            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();

        sleep(1);

        new Thread(()->{
            try {
                log.debug("taking {}", 1);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                log.debug("taking {}", 2);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

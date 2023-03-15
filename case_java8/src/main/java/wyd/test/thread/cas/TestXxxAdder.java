package wyd.test.thread.cas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TestXxxAdder {
    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            demo( () -> new AtomicLong(0),
                    l -> l.getAndIncrement()
            );
        }
        System.out.println("===========");
        for (int i = 0; i < 5; i++) {
            demo( () -> new LongAdder(),
                    l -> l.increment());
        }



    }

    public static <T> void demo(
            Supplier<T> supplier,
            Consumer<T> consumer
    ){
        T num = supplier.get();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ts.add(new Thread(()->{
                for (int j = 0; j < 500000; j++) {
                    consumer.accept(num);
                }
            }));
        }

        long start = System.nanoTime();
        ts.forEach(thread -> thread.start());
        ts.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println("cost: " + (end-start)/1000_000 + "; result: " + num);
    }
}

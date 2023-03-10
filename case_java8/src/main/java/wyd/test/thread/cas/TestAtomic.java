package wyd.test.thread.cas;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public class TestAtomic {
    public static void main(String[] args) {

        AtomicInteger i = new AtomicInteger(0);
                                                  //get add
        System.out.println(i.addAndGet(1)); // 1, 1
        System.out.println(i.getAndAdd(1)); // 1, 2
        System.out.println(i.incrementAndGet()); // 3, 3
        System.out.println(i.getAndIncrement()); // 3, 4
        System.out.println(i.decrementAndGet()); // 3, 3
        System.out.println(i.getAndDecrement()); // 3, 2

        System.out.println("======================");
        System.out.println(i);
        System.out.println(i.updateAndGet(x -> x * 10));

        System.out.println("======================");
        System.out.println(i.get());
        updateAndGet(i, x -> x + 100);
        System.out.println(i.get());
    }

    public static void updateAndGet(AtomicInteger i, IntUnaryOperator operator){
        while (true){
            int pre = i.get();
            int next = operator.applyAsInt(pre);
            if (i.compareAndSet(pre, next)) {
                break;
            }
        }
    }
}

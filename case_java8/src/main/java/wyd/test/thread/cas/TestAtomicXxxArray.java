package wyd.test.thread.cas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestAtomicXxxArray {

    public static void main(String[] args) {
        demo(
                ()->new int[10],
                (array)->array.length,
                (array, index) -> array[index]++,
                array -> System.out.println(Arrays.toString(array))
        );

        demo(
                ()->new AtomicIntegerArray(10),
                (array) -> array.length(),
                (array, index) -> array.incrementAndGet(index),
                array -> System.out.println(array)
        );
    }


    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFun,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer
    ){
        List<Thread> ts = new ArrayList<>();

        T array = arraySupplier.get();
        Integer length = lengthFun.apply(array);
        for (int i = 0; i < length; i++) {
            // 每个线程对数组作 10000 次操作
            ts.add(new Thread(()->{
                for (int j = 0; j < 10000; j++) {
                    putConsumer.accept(array, j % length);
                }
            }));
        }

        ts.forEach(t -> t.start());

        ts.forEach(t->{
            try{
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }); // 等待所有线程结束
        printConsumer.accept(array);
    }
}

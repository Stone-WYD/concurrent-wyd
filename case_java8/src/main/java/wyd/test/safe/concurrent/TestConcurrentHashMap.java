package wyd.test.safe.concurrent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class TestConcurrentHashMap {

    public static void main(String[] args) {
        demo(
                ()->new ConcurrentHashMap<String, LongAdder>(),
                (map, words)->{
                    for (String word : words) {
                        // 如果没查到单词，则新建一个LongAdder用于累加计数
                        LongAdder value = map.computeIfAbsent(word, (key) -> new LongAdder());
                        // 将取到的累加器＋1
                        value.increment();
                    }
                }
        );
    }

    private static <V> void demo(Supplier<Map<String, V>> supplier, BiConsumer<Map<String, V>, List<String>> consumer){
        Map<String, V> map = supplier.get();

        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            int idx = i + 1 ;
            Thread t = new Thread(()->{
                List<String> words = readFromFile(idx);
                consumer.accept(map, words);
            });
            ts.add(t);
        }
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println(map);
    }

    public static List<String> readFromFile(int i ){
        List<String> words = new ArrayList<>();
        try ( BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("tmp/" + i + ".txt")
                )
        )){
            while(true){
                String word = in.readLine();
                if (word == null) {
                    break;
                }
                words.add(word);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return words;
    }

}

package wyd.test.thread.immutable;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Slf4j(topic = "c")
public class Test1 {
    // 测试不可变对象

    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {

                try {
                    log.debug("{}", dtf.parse("1999-04-21"));
                } catch (Exception e) {
                    log.error("{}", e);
                }

            }).start();
        }
    }

    public static void test1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                synchronized (sdf) {
                    try {
                        log.debug("{}", sdf.parse("1999-04-21"));
                    } catch (Exception e) {
                        log.error("{}", e);
                    }
                }
            }).start();

        }
    }
}

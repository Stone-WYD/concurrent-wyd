package wyd.test.thread.create;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c")
public class Test1 {

    public static void main(String[] args) {
        log.debug("running");
        test1();
    }

    public static void test1(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        thread.setName("t1");
        thread.start();
    }
}

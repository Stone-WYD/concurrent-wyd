package wyd.test.thread.create;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c")
public class Test2 {

    public static void main(String[] args) {
        Runnable runable = () -> log.debug("running");

        Thread thread = new Thread(runable, "runable");
        thread.start();

    }
}

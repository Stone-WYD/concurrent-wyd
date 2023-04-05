package wyd.test.thread.interrupt;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class TestInterruptStop {
    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            long start = System.currentTimeMillis();
            log.debug("开始空转2s...");
            while (true){
                long end = System.currentTimeMillis();
                if ( end - start > 2000 ){
                    // 空转时间超过2s时停止
                    break;
                }
            }
            log.debug("空转结束，进入1s的睡眠...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.debug("t1 线程被打断了，运行结束。");
            }

        }, "t1" );
        t1.start();
        sleep(1);
        log.debug("主线程1s后打断t1。");
        t1.interrupt();

    }
}
// 总结：1. 运行中的线程如果没有判断是否被打断的逻辑，即使被调用interrupt方法也不会停止
//      2. 阻塞的线程在进入阻塞被调用interrupt方法打断过，进入阻塞后就会立即被打断
//      3. interrupt 不会立即终止线程，没有阻塞的线程不会被打断，想要线程在运行中被打断需要自己手写对应的打断逻辑。

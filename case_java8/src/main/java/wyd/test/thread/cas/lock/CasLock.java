package wyd.test.thread.cas.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class CasLock {
    // 学习使用，工作中不建议使用
    private AtomicInteger status = new AtomicInteger(0);

    public void lock(){
        while (true){
            if (status.compareAndSet(0, 1)) {
                break;
            }
        }
    }

    public void unlock(){
        log.debug("over...");
        status.set(0);
    }

    public static void main(String[] args) {
        CasLock casLock = new CasLock();
        new Thread(()->{
            log.debug("begin...");
            casLock.lock();
            try {
                sleep(1);
            }finally {
                casLock.unlock();
            }
        }, "t1" ).start();

        new Thread(()->{
            log.debug("begin...");
            casLock.lock();
            try {
                sleep(1);
            }finally {
                casLock.unlock();
            }
        }, "t2" ).start();
    }
}

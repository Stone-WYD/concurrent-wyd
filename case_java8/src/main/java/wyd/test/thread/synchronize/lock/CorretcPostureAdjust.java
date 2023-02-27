package wyd.test.thread.synchronize.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c")
public class CorretcPostureAdjust {

    static Boolean hasCigarette = false;
    static Boolean hasTakeout = false;
    static ReentrantLock ROOM = new ReentrantLock();
    static Condition waitCigaretteSet = ROOM.newCondition();
    static Condition waitTakeoutSet = ROOM.newCondition();

    public static void main(String[] args) throws InterruptedException {

        new Thread(()->{
            ROOM.lock();
            try {
                while (!hasCigarette) {
                    log.debug("没烟，先歇会...");
                    try {
                        waitCigaretteSet.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("可以开始干活了。。。");
            } finally {
                ROOM.unlock();
            }
        }, "小南").start();

        new Thread(()->{
            ROOM.lock();
            try {
                while (!hasTakeout) {
                    log.debug("外卖没到，再等会...");
                    try {
                        waitTakeoutSet.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("开始干活...");
            } finally {
                ROOM.unlock();
            }

        }, "小女").start();
        
        Thread.sleep(100);

        new Thread(()->{
            ROOM.lock();
            try{
                log.debug("外卖送到了...");
                hasTakeout = true;
                waitTakeoutSet.signal();
            }finally {
                ROOM.unlock();
            }
        }, "送外卖的").start();

        new Thread(()->{
            ROOM.lock();
            try{
                log.debug("烟送到了...");
                hasCigarette = true;
                waitCigaretteSet.signal();
            }finally {
                ROOM.unlock();
            }
        }, "送烟的").start();
        
    }
}

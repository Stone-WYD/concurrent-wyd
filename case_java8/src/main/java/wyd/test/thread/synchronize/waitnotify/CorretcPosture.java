package wyd.test.thread.synchronize.waitnotify;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c")
public class CorretcPosture {

    static final Object lock = new Object();
    static Boolean hasCigarette = false;
    static Boolean hasTakeout = false;

    public static void main(String[] args) throws InterruptedException {

        new Thread(()->{
            synchronized(lock){
                while (!hasCigarette){
                    log.debug("没烟，先歇会...");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("有烟没?[{}]",hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了。。。");
                }else {
                    log.debug("没干成活");
                }
            }

        }, "小南").start();

        new Thread(()->{
            synchronized (lock){
                while (!hasTakeout) {
                    log.debug("外卖没到，再等会...");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("外卖到了吗？[{}]", hasTakeout);
                if (hasTakeout){
                    log.debug("开始干活...");
                }else {
                    log.debug("外卖没到，没干成活。。。");
                }
            }
        }, "小女").start();
        
        Thread.sleep(100);
        synchronized (lock){
            log.debug("烟到了");
            hasCigarette = true;
            lock.notifyAll();
        }
        
        Thread.sleep(100);
        synchronized (lock){
            log.debug("外卖到了");
            hasTakeout = true;
            lock.notifyAll();
        }
        
    }
}

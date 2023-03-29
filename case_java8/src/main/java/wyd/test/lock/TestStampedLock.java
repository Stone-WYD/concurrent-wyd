package wyd.test.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

import static wyd.test.util.Sleeper.sleep;

public class TestStampedLock {
    public static void main(String[] args) {
        DataContainerStamped dataContainer = new DataContainerStamped(1);
        new Thread(()->{
            dataContainer.read(1);
        }, "t1").start();

        sleep(2);

        new Thread(()->{
            dataContainer.write(200);
        }, "t3").start();

        sleep(0.5);

        new Thread(()->{
            dataContainer.read(0);
        }, "t2").start();
    }
}

@Slf4j(topic = "c")
class DataContainerStamped {
    private int data;
    private final StampedLock lock = new StampedLock();

    public DataContainerStamped(int data) {
        this.data = data;
    }

    public int read(int readTime){
        long stamp = lock.tryOptimisticRead();
        log.debug("optimistic read locking...{}", stamp);
        sleep(readTime);
        if (lock.validate(stamp)){
            log.debug("optimistic read successfully! stamp:{}", stamp );
            return data;
        }
        log.debug("updating to read lock...{}", stamp);
        // 锁升级
        long readLockStamp = lock.readLock();
        try {
            log.debug("get read lock.");
            sleep(readTime);
            log.debug("read finish.");
            return data;
        } finally {
            log.debug("readLock unlock, stamp:{}", readLockStamp);
            lock.unlockRead(readLockStamp);
        }
    }

    public int write(int newData){
        long stamp = lock.writeLock();
        log.debug("write lock {}", stamp);
        try {
            sleep(2);
            this.data = newData;
        } finally {
            log.debug("write unlock {}", stamp);
            lock.unlockWrite(stamp);
        }
        return 0;
    }
}

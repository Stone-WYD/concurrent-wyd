package wyd.test.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class TestAqs {
    public static void main(String[] args) {
        MyLock myLock = new MyLock();
        new Thread(()->{
            myLock.lock();
            try {
                log.debug("locking...");
                sleep(1);
            }finally {
                log.debug("unlocking...");
                myLock.unlock();
            }
        }).start();

        new Thread(()->{
            myLock.lock();
            try {
                log.debug("locking...");
            }finally {
                log.debug("unlocking...");
                myLock.unlock();
            }
        }).start();

    }
}


// 自定义锁（不可重入锁）
class MyLock implements Lock{

    // 独占锁 同步器类
    class MySync extends AbstractQueuedSynchronizer{

        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        @Override // 是否持有独占锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition(){
            return new ConditionObject();
        }
    }

    private MySync mySync = new MySync();

    @Override // 加锁，不成功会进入等待队列
    public void lock() {
        mySync.acquire(1);
    }

    @Override // 加锁，可打断
    public void lockInterruptibly() throws InterruptedException {
        mySync.acquireInterruptibly(1);
    }

    @Override // 加锁，尝试一次
    public boolean tryLock() {
        return mySync.tryAcquire(1);
    }

    @Override // 加锁，带超时
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mySync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        mySync.release(1);
    }

    @Override
    public Condition newCondition() {
        return mySync.newCondition();
    }
}
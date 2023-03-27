package wyd.test.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class TestReadWriteLock {
    public static void main(String[] args) {
        DataContainer dc = new DataContainer();

        new Thread(()->{
            dc.write(new Object());
        }, "w1").start();

        sleep(0.5);

        new Thread(()->{
            dc.read();
        }, "r1").start();

        new Thread(()->{
            dc.read();
        }, "r2").start();


    }
}

@Slf4j(topic = "c")
class DataContainer {
    private Object data;
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    ReentrantReadWriteLock.ReadLock r = rw.readLock();
    ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public Object read(){
        r.lock();
        log.debug("获取读锁");
        try {
            log.debug("读取");
            sleep(1);
            return data;
        }finally {
            log.debug("释放读锁");
            r.unlock();
        }
    }

    public void write( Object data ){

        w.lock();
        log.debug("获取写锁");
        try{
            log.debug("写入");
            sleep(2);
            this.data = data;
            return;
        }finally {
            log.debug("释放写锁");
            w.unlock();
        }
    }
}

package wyd.test.thread.model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class RuningAlternately {
    static Thread t1 , t2 , t3 ;

    public static void main(String[] args) throws Exception{
        useParkUnPark();
    }

    public static void useWaitNotify(){

        WaitNotify wn = new WaitNotify(1, 5);
        new Thread(()->{
            wn.print("a", 1, 2);
        }, "t1").start();
        new Thread(()->{
            wn.print("b", 2, 3);
        }, "t2").start();
        new Thread(()->{
            wn.print("c", 3, 1);
        }, "t3").start();

    }

    public static void useAwaitSignal() throws InterruptedException {
        AwaitSignal as = new AwaitSignal(5);
        Condition a = as.newCondition();
        Condition b = as.newCondition();
        Condition c = as.newCondition();
        new Thread(()->{
            as.print("a", a, b);
        }, "t1").start();

        new Thread(()->{
            as.print("b", b, c);
        }, "t2").start();

        new Thread(()->{
            as.print("c", c, a);
        }, "t3").start();

        System.out.println("开始");
        Thread.sleep(100);
        as.lock();
        a.signal();
        as.unlock();
    }

    public static void useParkUnPark() throws InterruptedException {
        ParkUnpark parkUnpark = new ParkUnpark(5);
        t1 = new Thread(()->{
            parkUnpark.print("a", t2);
        }, "t1");
        t2 = new Thread(()->{
            parkUnpark.print("b", t3);
        }, "t2");
        t3 = new Thread(()->{
            parkUnpark.print("c", t1);
        }, "t3");
        t1.start();
        t2.start();
        t3.start();

        System.out.println("开始...");
        Thread.sleep(100);
        LockSupport.unpark(t1);

    }
}

class WaitNotify{
    // 打印
    public void print(String content, int flag, int nextFlag){
        for (int i = 0; i < this.loopNumber; i++) {
            synchronized (this){
                while(this.flag != flag){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.print(content);
                this.flag = nextFlag;
                this.notifyAll();
            }
        }
    }

    // 等待标记
    private int flag;
    // 循环次数
    private int loopNumber;

    public WaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }
}

class AwaitSignal extends ReentrantLock{

    public void print(String str, Condition c1, Condition c2){
        for (int i = 0; i < loopNum; i++) {
            this.lock();
            try {
                c1.await();
                System.out.print(str);
                c2.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                this.unlock();
            }
        }
    }

    private int loopNum;

    public AwaitSignal(int loopNum) {
        this.loopNum = loopNum;
    }
}

class ParkUnpark{
    public void print(String str, Thread next){
        for (int i = 0; i < loopNum; i++) {
            LockSupport.park();
            System.out.print(str);
            LockSupport.unpark(next);
        }
    }

    private int loopNum;

    public ParkUnpark(int loopNum) {
        this.loopNum = loopNum;
    }
}
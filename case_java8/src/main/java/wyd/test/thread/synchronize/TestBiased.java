package wyd.test.thread.synchronize;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c")
public class TestBiased {

    static Thread t1, t2, t3;

    public static void main(String[] args) throws InterruptedException {

    }

    // 测试批量重偏向
    public static void test1(){

        Dog d = new Dog();

        new Thread(()->{
            log.debug(ClassLayout.parseInstance(d).toPrintableSimple());
            synchronized (d){
                log.debug(ClassLayout.parseInstance(d).toPrintableSimple());
            }
            log.debug(ClassLayout.parseInstance(d).toPrintableSimple());

            synchronized (TestBiased.class){
                TestBiased.class.notify();
            }
        },"t1").start();

        new Thread(()->{

            synchronized (TestBiased.class){
                try {
                    TestBiased.class.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            log.debug(ClassLayout.parseInstance(d).toPrintableSimple());
            synchronized (d){
                log.debug(ClassLayout.parseInstance(d).toPrintableSimple());
            }
            log.debug(ClassLayout.parseInstance(d).toPrintableSimple());
        },"t2").start();
    }

    // 测试批量撤销
    public static void test2(){
        Vector<Dog> list = new Vector<>();
        int loopNumber = 39;

        t1 = new Thread(()->{
            for (int i = 0; i < loopNumber; i++) {
                Dog dog = new Dog();
                list.add(dog);
                log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

        t2 = new Thread(()->{
            LockSupport.park();
            log.debug("========================>");
            // 撤销重偏向20次后，批量重偏向t2

            LockSupport.unpark(t3);
        }, "t2");
        t2.start();

        t3 = new Thread(()->{
            LockSupport.park();



        }, "t3");
        t3.start();
    }

}

class Dog {

}

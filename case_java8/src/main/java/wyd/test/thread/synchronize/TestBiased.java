package wyd.test.thread.synchronize;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c")
public class TestBiased {

    static Thread t1, t2, t3;

    public static void main(String[] args) throws InterruptedException {
        test22();
    }

    // 测试偏向锁的撤销
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

    // 测试批量重偏向
    public static void test2() throws InterruptedException{
        Vector<Dog> list = new Vector<>();
        Cat cat = new Cat();

        t1 = new Thread(()->{
            for (int i = 0; i < 30; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d){
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                }
            }

            synchronized (cat){
                log.debug("cat" + "\t" + ClassLayout.parseInstance(cat).toPrintableSimple());
            }

            synchronized (list){
                list.notify();
            }
        },"t1");
        t1.start();

        t2 = new Thread(()->{

            synchronized (list){
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int i = 0; i < 30; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
            }

            log.debug("cat" + "\t" + ClassLayout.parseInstance(cat).toPrintableSimple());
            synchronized (cat){
                log.debug("cat" + "\t" + ClassLayout.parseInstance(cat).toPrintableSimple());
            }
            log.debug("cat" + "\t" + ClassLayout.parseInstance(cat).toPrintableSimple());
        }, "t2");
        t2.start();
    }

    // 测试重偏向-2
    public static void test22() throws InterruptedException{
        Vector<Dog> list = new Vector<>();

        t1 = new Thread(()->{
            for (int i = 0; i < 30; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d){
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                }
            }

            synchronized (list){
                list.notify();
            }
        },"t1");
        t1.start();

        t2 = new Thread(()->{

            synchronized (list){
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int i = 0; i < 19; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
            }

            LockSupport.unpark(t3);
        }, "t2");
        t2.start();

        t3 = new Thread(()->{

            LockSupport.park();

            for (int i = 19; i < 30; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple());
            }

        }, "t3");
        t3.start();

    }

    // 测试批量撤销
    public static void test3(){
        Vector<Dog> list = new Vector<>();
        int loopNumber = 39;

        t1 = new Thread(()->{
            for (int i = 0; i < loopNumber; i++) {
                Dog dog = new Dog();
                list.add(dog);
                synchronized (dog){
                    log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
                }
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

        t2 = new Thread(()->{
            LockSupport.park();
            log.debug("========================>");
            // 撤销重偏向20次后，批量重偏向t2
            for (int i = 0; i < loopNumber; i++) {
                Dog dog = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
                synchronized (dog){
                    log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
            }
            LockSupport.unpark(t3);
        }, "t2");
        t2.start();

        t3 = new Thread(()->{
            LockSupport.park();
            log.debug("========================>");
            for (int i = 0; i < loopNumber; i++) {
                Dog dog = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
                synchronized (dog){
                    log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(dog).toPrintableSimple());
            }
        }, "t3");
        t3.start();
    }

}

class Dog {

}

class Cat {

}

package wyd.test.thread.synchronize;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

@Slf4j(topic = "c")
public class TestBiased {

    public static void main(String[] args) throws InterruptedException {
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
}

class Dog {

}

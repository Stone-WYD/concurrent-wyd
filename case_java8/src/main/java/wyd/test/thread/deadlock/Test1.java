package wyd.test.thread.deadlock;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c")
public class Test1 {

    public static void main(String[] args) {
        ClassA a = new ClassA();
        ClassB b = new ClassB();

        new Thread(()->{
            synchronized (a){
                log.debug("lock a...");
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (b){
                    log.debug("lock b...");
                }

            }
        }, "threadA").start();

        new Thread(()->{
            synchronized (b){
                log.debug("lock b...");
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (a){
                    log.debug("lock a...");
                }
            }
        }, "threadB").start();
    }
}
@Slf4j(topic = "c")
class ClassA{

}
@Slf4j(topic = "c")
class ClassB{

}

/*
* 检测死锁方法：
* 一、
*  1. jps查看线程
*  2. jstack + 进程id
* 二、
*  使用jconsole
* */

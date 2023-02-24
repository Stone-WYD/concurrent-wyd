package wyd.test.thread.model;

import lombok.extern.slf4j.Slf4j;
import wyd.test.util.Downloader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "c")
public class ProtectPause {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new Person().start();
        }

        TimeUnit.SECONDS.sleep(1);

        for (Integer id : MailBoxes.getIds()) {
            new Postman(id, "内容" + id).start();
        }
    }

    public static void test1(){
        GuardedObject guardedObject = new GuardedObject(null, null);
        new Thread(()->{
            // 消费线程
            try {
                List<String> response = (List<String>) guardedObject.get(1000);
                log.debug("数据大小为：{}", response.size());
                response.stream().forEach(p -> log.debug("数据内容为：{}" + p ));

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t1").start();


        new Thread(()->{
            // 生产者线程
            try {
                List<String> response = Downloader.download();
                log.debug("生产者生产数据中...");
                guardedObject.complete(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "t2").start();
    }

    public static void test2(){
        GuardedObject guardedObject = new GuardedObject(null, null);

        new Thread(()->{
            try {
                log.debug("begin...");
                Object o = guardedObject.get(2000);
                log.debug("获取到的结果为：{}", o);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t1").start();


        new Thread(()->{
            log.debug("begin...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            guardedObject.complete(null);
        }, "t2").start();
    }

}
@Slf4j(topic = "c.Person")
class Person extends Thread{
    @Override
    public void run() {
        GuardedObject guardedObject = MailBoxes.createGuardedObject();
        // 开始收信
        try {
            log.debug("开始收信...");
            Object o = guardedObject.get(5000);
            log.debug("id为: {},收到的内容为: {}", guardedObject.getId(), o);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
@Slf4j(topic = "c.Postman")
class Postman extends Thread{

    private Integer id;
    private Object mail;

    public Postman(Integer id, Object mail){
        this.id = id;
        this.mail = mail;
    }
    @Override
    public void run() {
        GuardedObject guardedObject = MailBoxes.getGuardedObject(id);
        log.debug("送信 id:{}, 内容：{}", id, mail);
        guardedObject.complete(mail);
    }
}

class MailBoxes{
    private static Map<Integer, GuardedObject> boxes = new ConcurrentHashMap<>();
    private static AtomicInteger id = new AtomicInteger(0);

    // 取GuardedObject使用
    public static synchronized GuardedObject createGuardedObject(){
        GuardedObject guardedObject = new GuardedObject(id.incrementAndGet(), null);
        boxes.put(guardedObject.getId(), guardedObject);
        return guardedObject;
    }

    // 生产GuardedObject使用
    public static GuardedObject getGuardedObject(Integer id){
        return boxes.remove(id);
    }

    public static Set<Integer> getIds(){
        return boxes.keySet();
    }

}

class GuardedObject{
    private Object response;

    private Integer id;

    public GuardedObject(Integer id, Object response){
        this.id = id;
        this.response = response;
    }

    public Integer getId() {
        return id;
    }


    // 获取结果
    public Object get(long timeout) throws InterruptedException {
        synchronized(this){

            // 开始的时间
            long startTime = System.currentTimeMillis();
            // 经过的时间
            long passedTime = 0;
            while (response == null) {
                // 这一轮需要等待的时间
                long waitTime = timeout - passedTime;
                if (waitTime <= 0) {
                    break;
                }
                this.wait(waitTime);
                passedTime = System.currentTimeMillis() - startTime;
            }
        }
        return response;
    }

    // 存放结果
    public void complete(Object response){
        synchronized (this){
            this.response = response;
            this.notifyAll();
        }
    }

}

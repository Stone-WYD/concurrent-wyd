package wyd.test.thread.synchronize.waitnotify;

import lombok.extern.slf4j.Slf4j;
import wyd.test.util.Downloader;

import java.io.IOException;
import java.util.List;
@Slf4j(topic = "c")
public class ProtectPause {

    public static void main(String[] args) {

        GuardedObject guardedObject = new GuardedObject();
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

}

class GuardedObject{
    private Object response;

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
                passedTime = startTime - System.currentTimeMillis();
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

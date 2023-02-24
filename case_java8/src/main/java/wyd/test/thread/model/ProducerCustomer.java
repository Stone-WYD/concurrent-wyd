package wyd.test.thread.model;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class ProducerCustomer {

    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue(2);

        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(()->{
                messageQueue.put(new Message(id, new Object()));
            },"生产者" + i ).start();
        }

        new Thread(()->{
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                messageQueue.take();
            }
        }, "消费者").start();
    }
}

@Slf4j(topic = "c")
class MessageQueue{

    private LinkedList<Message> queue = new LinkedList<>();
    private int capcity;

    public MessageQueue(int capcity) {
        this.capcity = capcity;
    }

    public void put(Message message){
        synchronized (queue){
            while (queue.size() == capcity){
                log.debug("队列已满，等待消费...");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("产生了消息: {}", message);
            queue.addLast(message);
            queue.notifyAll();
        }
    }

    public Message take(){
        synchronized(queue){
            while (queue.isEmpty()){
                // 没有消息时等待消息
                log.debug("队列已空，等待生产...");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Message message = queue.removeFirst();
            log.debug("消费了消息: {}", message);
            queue.notifyAll();
            return message;
        }
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

}

class Message{
    private int id;
    private Object object;

    public int getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }

    public Message(int id, Object object) {
        this.id = id;
        this.object = object;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", object=" + object +
                '}';
    }
}

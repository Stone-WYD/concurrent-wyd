package wyd.test.thread.deadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Test2 {
    public static void main(String[] args) {
        Chopstick c1 = new Chopstick();
        Chopstick c2 = new Chopstick();
        Chopstick c3 = new Chopstick();
        Chopstick c4 = new Chopstick();
        Chopstick c5 = new Chopstick();

        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c1, c2).start();
        new Philosopher("亚里士多德", c1, c2).start();
        new Philosopher("赫拉克利特", c1, c2).start();
        new Philosopher("阿基米德", c1, c2).start();
    }
}

@Slf4j(topic = "c")
class Philosopher extends Thread{
    Chopstick left;
    Chopstick right;

    Random random = new Random();

    public Philosopher(String name, Chopstick left, Chopstick right){
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while (true){
            synchronized (left){
                synchronized (right){
                    eat();
                }
            }
        }
    }

    private void eat(){
        log.debug("eating...");
        try {
            TimeUnit.SECONDS.sleep(random.nextInt(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Chopstick{

}

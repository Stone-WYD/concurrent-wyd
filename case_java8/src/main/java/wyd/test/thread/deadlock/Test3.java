package wyd.test.thread.deadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Test3 {
    public static void main(String[] args) {
        Chopstick1 c1 = new Chopstick1();
        Chopstick1 c2 = new Chopstick1();
        Chopstick1 c3 = new Chopstick1();
        Chopstick1 c4 = new Chopstick1();
        Chopstick1 c5 = new Chopstick1();

        new Philosopher1("苏格拉底", c1, c2).start();
        new Philosopher1("柏拉图", c2, c3).start();
        new Philosopher1("亚里士多德", c3, c4).start();
        new Philosopher1("赫拉克利特", c4, c5).start();
        new Philosopher1("阿基米德", c5, c1).start();
    }
}

@Slf4j(topic = "c")
class Philosopher1 extends Thread{
    Chopstick1 left;
    Chopstick1 right;

    Random random = new Random();

    public Philosopher1(String name, Chopstick1 left, Chopstick1 right){
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while (true){
            // 尝试能否拿到左手筷子
            if (left.tryLock()) {
                try{
                    // 尝试能否拿到右手筷子
                    if (right.tryLock()){
                        try{
                            eat();
                        }finally {
                            right.unlock();
                        }
                    }
                }finally {
                    /*很重要，因为没拿到两支筷子会主动释放，
                    而不会一直等待另一支筷子*/
                    left.unlock();
                }
                try {
                    TimeUnit.SECONDS.sleep(random.nextInt(2));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void eat(){
        log.debug("eating...");
        try {
            TimeUnit.SECONDS.sleep(random.nextInt(2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Chopstick1 extends ReentrantLock {

}

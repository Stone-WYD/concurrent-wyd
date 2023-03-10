package wyd.test.thread.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

import static wyd.test.util.Sleeper.sleep;

@Slf4j(topic = "c")
public class TestAtomicMark {
    public static void main(String[] args) {
        Garbage garbage = new Garbage("垃圾袋满了...");
        AtomicMarkableReference<Garbage> bag = new AtomicMarkableReference<>(garbage, true);

        new Thread(() -> {
            if (bag.compareAndSet(garbage, garbage, true, false)) {
                log.debug("t1 清空了垃圾袋...");
            }
        }, "t1"). start();

        sleep(1);

        Garbage newGarbage = new Garbage("新垃圾袋");
        if (bag.compareAndSet(garbage, newGarbage, true, false)) {
            log.debug("主线程换垃圾袋了" + newGarbage );
        }else {
            log.debug("垃圾袋被t1线程清空了...");
        }

    }
}

class Garbage{
    private String desc;

    public Garbage(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Garbage{" +
                "desc='" + desc + '\'' +
                '}';
    }
}

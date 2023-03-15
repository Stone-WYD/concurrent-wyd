package wyd.test.thread.cas;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;
import wyd.test.thread.cas.unsafe.TestUnsafe;

@Slf4j(topic = "c")
public class MyAtomicIntegerTest {
    public static void main(String[] args) {
        Account.demo(new MyAtomicInteger(10000));
    }
}

class MyAtomicInteger implements Account {

    private volatile int value;
    private static Unsafe UNSAFE;
    private static long valueOffset;
    static {
        UNSAFE = TestUnsafe.getUnsafe();
        try {
            valueOffset = UNSAFE.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int getValue(){
        return value;
    }

    public void decrement( int amount ){
        while (true) {
            int pre = getValue();
            int next = pre - amount;
            if (UNSAFE.compareAndSwapInt(value, valueOffset, pre, next)) {
                break;
            }
        }
    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }

    @Override
    public Integer getBalance() {
        return getValue();
    }

    @Override
    public void withdraw(Integer amount) {
        decrement(amount);
    }
}

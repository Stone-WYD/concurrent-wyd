package wyd.test.thread.cas.unsafe;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class TestUnsafe {

    public static void main(String[] args) throws Exception {

        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        // 真正的unsafe对象
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        System.out.println(unsafe);

        // 1.获取域的偏移地址
        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));

        Teacher t = new Teacher();
        // 2.执行 cas 操作
        unsafe.compareAndSwapInt(t, idOffset, 0, 1 );
        unsafe.compareAndSwapObject(t, nameOffset, null, "张三");

        // 3.验证
        System.out.println(t);
    }

    public static Unsafe getUnsafe(){
        Unsafe unsafe;
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return unsafe;
    }
}

@Data
class Teacher {
    volatile int id;
    volatile String name;
}

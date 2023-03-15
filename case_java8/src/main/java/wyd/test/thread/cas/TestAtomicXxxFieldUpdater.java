package wyd.test.thread.cas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class TestAtomicXxxFieldUpdater {

    public static void main(String[] args) {
        List<Thread> ts = new ArrayList<>();
        Student student = new Student();
        student.setName("李白");
        for (int i = 0; i < 100; i++) {
            int num = i;
            ts.add(new Thread( () -> {
                student.setName( "t" + num);
            }));
        }
        ts.forEach(thread -> thread.start());
        ts.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("student最终名称为：" + student.getName());
    }

}

class Student{

    private volatile String name;

    private AtomicReferenceFieldUpdater fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");

    public String getName() {
        return name;
    }

    public void setName( String name ){
        while (fieldUpdater.compareAndSet(this, getName(), name)) {
            return;
        }
    }
}

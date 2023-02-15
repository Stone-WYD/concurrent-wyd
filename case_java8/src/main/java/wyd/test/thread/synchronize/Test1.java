package wyd.test.thread.synchronize;

public class Test1 {

    public static void main(String[] args) throws InterruptedException {

        Room room = new Room();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                room.increment();
            }
        }, "t1");

        Thread t2 = new Thread(()->{
            for (int i = 0; i < 999; i++) {
                room.decrement();
            }
        }, "t2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(room.getCount());

    }

    static class Room{
        private int count = 0;

        public void increment(){
            synchronized(this){
                count ++ ;
            }
        }

        public void decrement(){
            synchronized (this){
                count --;
            }
        }

        public int getCount() {
            return count;
        }
    }
}

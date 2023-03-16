package wyd.test.thread.immutable.pool;

import java.sql.Connection;
import java.util.Random;

public class PoolTest {
    public static void main(String[] args) {

        Pool pool = new Pool(2);
        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                Connection connection = pool.getConnection();
                try{
                    Thread.sleep(new Random().nextInt(1000));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    pool.free(connection);
                }
            }).start();
        }
    }
}

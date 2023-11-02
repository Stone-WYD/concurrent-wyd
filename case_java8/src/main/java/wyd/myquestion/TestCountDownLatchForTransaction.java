package wyd.myquestion;

import lombok.extern.slf4j.Slf4j;
import wyd.test.util.Sleeper;

import java.util.concurrent.CountDownLatch;

/**
 * @program: concurrent-wyd
 * @description: 多线程事务测试
 * @author: Stone
 * @create: 2023-11-02 10:56
 **/
public class TestCountDownLatchForTransaction {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(3);

        SqlWithTransactionTask task1 = new SqlWithTransactionTask(latch, false);
        SqlWithTransactionTask task2 = new SqlWithTransactionTask(latch, false);
        SqlWithTransactionTask task3 = new SqlWithTransactionTask(latch, true);

        new Thread(task1, "t1").start();
        new Thread(task2, "t2").start();
        new Thread(task3, "t3").start();

        latch.await();

        if (SqlWithTransactionTask.success) {
            System.out.println("主线程：事务执行成功！！");
        } else {
            System.out.println("主线程：事务执行失败！！");
        }


    }

    static class SqlWithTransactionTask implements Runnable{

        public static volatile boolean success = true;
        private CountDownLatch latch;
        private Boolean canCommit;

        public SqlWithTransactionTask(CountDownLatch latch, Boolean canCommit) {
            this.latch = latch;
            this.canCommit = canCommit;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + "线程正在执行 sql 中...");
            Sleeper.sleep(1);
            if (!canCommit) {
                success = false;
                latch.countDown();
                throw new RuntimeException(threadName + "线程在 sql 运行过程中出现问题，需要回滚!!!");
            }
            latch.countDown();

            try {
                latch.await();
                if (success) {
                    System.out.println("线程" + threadName + ": 事务执行完成");
                } else {
                    System.out.println("事务中其他线程执行时出现异常，导致" + threadName + "进行回滚...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RecursiveTask;

@Slf4j(topic = "c")
public class TestForkJoin {

    public static void main(String[] args) {
        MyTask myTask = new MyTask(5);
        Integer result = myTask.compute();
        System.out.println(result);
    }
}

@Slf4j(topic = "c")
class MyTask extends RecursiveTask<Integer> {

    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {

        if (n == 1) {
            return n;
        }

        // 将任务拆分
        MyTask t1 = new MyTask(n - 1);
        t1.fork();
        log.debug("fork() {} + {}", n, t1);

        // 合并join 结果
        int result = n + t1.join();
        log.debug("join() {} + {}", n, t1, result);
        return result;
    }

    @Override
    public String toString() {
        return "MyTask{" +
                "n=" + n +
                '}';
    }
}
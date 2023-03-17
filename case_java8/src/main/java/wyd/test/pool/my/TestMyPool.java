package wyd.test.pool.my;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c")
public class TestMyPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2, 1000,
                TimeUnit.MILLISECONDS, 2, (queue, task)->{
            // 死等
            //queue.put(task);
            // 带超时的等待
            //queue.offer(1000, TimeUnit.MILLISECONDS, task);
            // 放弃执行任务
            //log.debug("队列已满，放弃执行任务{}", task);
            // 调用者抛出异常  主线程后面的任务不会再被调用
            //throw new RuntimeException("任务执行失败 " + task);
            // 调用者自己执行
            task.run();
        });
        for (int i = 0; i < 6; i++) {
            int j = i;
            threadPool.execute(()->{
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("任务内日志：{}", j);
            });
        }

    }
}

@Slf4j(topic = "c")
class ThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    private HashSet<Worker> workers = new HashSet<>();

    // 核心线程数
    private int coreSize;

    // 超时时间
    private long timeout;

    private TimeUnit timeUnit;

    // 拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit,
                      int queueCapcity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.rejectPolicy = rejectPolicy;
    }

    public void execute(Runnable task){
        // 任务数量少于核心线程数
        synchronized (workers){
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                workers.add(worker);
                log.debug("创建了新线程{}: 来执行任务 {}...", worker, task);
                worker.start();
            }else {
                log.debug("将任务 {} 放入了阻塞队列...",task);
                //taskQueue.put(task);
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }

    class Worker extends Thread{
        private Runnable task ;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 1）当task不为空，直接执行
            // 2）task为空，从任务队列中取出任务来执行
            //while(task!=null || (task = taskQueue.take())!=null ){
            while(task!=null || (task = taskQueue.poll(timeout, timeUnit))!=null ){
                try {
                    log.debug("正在执行任务 {} ...", task);
                    task.run();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    task = null;
                }
            }
            // 移出worker
            synchronized (workers){
                log.debug("worker {} 被移出了", this);
                workers.remove(this);
            }
        }
    }
}

@Slf4j(topic = "c")
class BlockingQueue<T> {
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2.锁
    private ReentrantLock lock = new ReentrantLock();

    // 3. 生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    // 4. 消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    // 5. 容量
    private int capcity;

    public BlockingQueue(int capcity) {
        this.capcity = capcity;
    }

    // 带超时的阻塞获取
    public T poll(long timeout, TimeUnit unit){
        lock.lock();
        try {
            // 将超时时间统一转换为 纳秒
            long nanos = unit.toNanos(timeout);
            while(queue.isEmpty()){
                try {
                    if (nanos <= 0) {
                        return null;
                    }
                    // 避免虚假唤醒：唤醒之后却没有获取到资源
                    nanos = emptyWaitSet.awaitNanos(nanos);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }
    }

    // 阻塞获取
    public T take(){
        lock.lock();
        try {
            while(queue.isEmpty()){
                try {
                    emptyWaitSet.await();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }
    }

    // 带超时时间的阻塞添加
    public boolean offer(long timeout, TimeUnit unit, T element){
        lock.lock();
        try{
            long nanos = unit.toNanos(timeout);
            while(queue.size() == capcity){
                try{
                    if (nanos <= 0) {
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            queue.push(element);
            emptyWaitSet.signal();
            return true;
        }finally {
            lock.unlock();
        }
    }

    // 阻塞添加
    public void put(T element){
        lock.lock();
        try{
            while(queue.size() == capcity){
                try{
                    fullWaitSet.await();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            queue.push(element);
            log.debug("阻塞队列添加了任务: {}", element);
            emptyWaitSet.signal();
        }finally {
            lock.unlock();
        }
    }

    // 有拒绝策略地添加
    public void tryPut(RejectPolicy rejectPolicy, T task){
        lock.lock();
        try {
            if (queue.size() < capcity){
                // 还可以添加任务
                queue.push(task);
                log.debug("阻塞队列添加了任务: {}", task);
            }else {
                // 执行拒绝策略
                rejectPolicy.reject(this, task);
            }
        }finally {
            lock.unlock();
        }
    }
}

@FunctionalInterface
interface RejectPolicy<T>{
    void reject(BlockingQueue<T> queue, T task);
}



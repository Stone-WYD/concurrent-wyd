package wyd.test.thread.interrupt;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.Test6")
public class Test3 {

    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();
        TimeUnit.SECONDS.sleep(3);
        tpt.stop();
    }

    @Slf4j(topic = "c.TwoPhaseTermination")
    static class TwoPhaseTermination{
        private Thread monitor;

        // 判断是否该结束
        private volatile boolean stop;
        // 判断是否已经执行过strat
        private boolean starting = false ;

        public void start(){
            synchronized (this) {
                // 犹豫模式
                if (starting) {
                    return;
                }
                starting = true;
            }
            this.monitor = new Thread(() -> {
                while (true){
                    if (Thread.currentThread().isInterrupted()) {
                        log.debug("料理后事...");
                        break;
                    }
                    // 用 volatile 的 stop 变量控制结束
                    if (stop) {
                        log.debug("料理后事....");
                        break;
                    }
                try {
                        TimeUnit.SECONDS.sleep(1);
                        log.debug("打印监控日志...");
                    } catch (InterruptedException e) {
                    // 如果使用 stop 变量结束，此处可以不重置打断标记，也不用打印日志
                        e.printStackTrace();
                        // 重置打断标记
                        monitor.interrupt();
                    }
                }
            }, "mointor");
            monitor.start();
        }

        public void stop(){
            monitor.interrupt();
        }

        public void stop2(){
            stop = true;
            monitor.interrupt();
        }
    }
}




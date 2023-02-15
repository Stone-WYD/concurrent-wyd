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

        public void start(){

            this.monitor = new Thread(() -> {
                while (true){
                    if (Thread.currentThread().isInterrupted()) {
                        log.debug("料理后事...");
                        break;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        log.debug("打印监控日志...");
                    } catch (InterruptedException e) {
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
    }
}




package wyd.test.pool.jdk;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c")
public class TestSchedule {

    // 固定每周四 18:00 定时执行任务
    public static void main(String[] args) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取周四时间
        LocalDateTime time = now.withHour(18).withMinute(0).withSecond(0).
                withNano(0).with(DayOfWeek.THURSDAY);
        if (now.compareTo(time)>0){
            time = time.plusWeeks(1);
        }

        long initailDelay = Duration.between(now, time).toMillis();
        long period = 1000 * 60 * 60 * 24 * 7;
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(()->{
            System.out.println("running...");
        }, initailDelay, period, TimeUnit.MILLISECONDS);

    }
}

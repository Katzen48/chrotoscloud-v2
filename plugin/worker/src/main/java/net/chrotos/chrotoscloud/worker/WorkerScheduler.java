package net.chrotos.chrotoscloud.worker;

import net.chrotos.chrotoscloud.tasks.Scheduler;

import java.time.Duration;
import java.util.concurrent.*;

public class WorkerScheduler implements Scheduler {
    private final ScheduledExecutorService executorService;

    public WorkerScheduler() {
        this.executorService = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void runTask(Runnable runnable) {
        runTaskAsync(runnable);
    }

    @Override
    public void runTaskLater(Runnable runnable, Duration delay) {
        runTaskLaterAsync(runnable, delay);
    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        runTaskLaterAsync(runnable, Duration.ZERO);
    }

    @Override
    public void runTaskLaterAsync(Runnable runnable, Duration delay) {
        executorService.schedule(runnable, delay.toSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void runTaskTimer(Runnable runnable, Duration delay, Duration period) {
        runTaskTimerAsync(runnable, delay, period);
    }

    @Override
    public void runTaskTimerAsync(Runnable runnable, Duration delay, Duration period) {
        executorService.scheduleAtFixedRate(runnable, delay.toSeconds(), period.toSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}

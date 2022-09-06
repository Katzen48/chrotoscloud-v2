package net.chrotos.chrotoscloud.tasks;

import java.time.Duration;

public interface Scheduler extends AutoCloseable {
    void runTask(Runnable runnable);
    void runTaskLater(Runnable runnable, Duration delay);
    void runTaskAsync(Runnable runnable);
    void runTaskLaterAsync(Runnable runnable, Duration delay);
    void runTaskTimer(Runnable runnable, Duration delay, Duration period);
    void runTaskTimerAsync(Runnable runnable, Duration delay, Duration period);
}

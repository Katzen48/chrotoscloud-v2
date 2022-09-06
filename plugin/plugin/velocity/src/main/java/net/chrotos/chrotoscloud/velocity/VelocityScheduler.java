package net.chrotos.chrotoscloud.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import net.chrotos.chrotoscloud.tasks.Scheduler;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;

public class VelocityScheduler implements Scheduler {
    private final VelocityCloud cloud;
    private ScheduledTask cleanupTask;
    private final CopyOnWriteArrayList<ScheduledTask> tasks = new CopyOnWriteArrayList<>();

    @Inject
    public VelocityScheduler(VelocityCloud cloud) {
        this.cloud = cloud;
    }

    protected void initialize() {
        cleanupTask = buildTask(() -> tasks.removeIf((task) -> task.status() != TaskStatus.SCHEDULED)).delay(Duration.ofSeconds(2L))
                .repeat(Duration.ofSeconds(2L)).schedule();
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
        tasks.add(cloud.getProxyServer().getScheduler().buildTask(cloud.getPlugin(), runnable).schedule());
    }

    @Override
    public void runTaskLaterAsync(Runnable runnable, Duration delay) {
        tasks.add(cloud.getProxyServer().getScheduler().buildTask(cloud.getPlugin(), runnable).delay(delay).schedule());
    }

    @Override
    public void runTaskTimer(Runnable runnable, Duration delay, Duration period) {
        runTaskTimerAsync(runnable, delay, period);
    }

    @Override
    public void runTaskTimerAsync(Runnable runnable, Duration delay, Duration period) {
        tasks.add(cloud.getProxyServer().getScheduler().buildTask(cloud.getPlugin(), runnable).delay(delay).repeat(period).schedule());
    }

    private com.velocitypowered.api.scheduler.Scheduler.TaskBuilder buildTask(Runnable runnable) {
        return cloud.getProxyServer().getScheduler().buildTask(cloud.getPlugin(), runnable);
    }

    @Override
    public void close() {
        if (cleanupTask != null && cleanupTask.status() == TaskStatus.SCHEDULED) {
            cleanupTask.cancel();
        }

        tasks.removeIf((task) -> task.status() != TaskStatus.SCHEDULED);
        tasks.forEach(ScheduledTask::cancel);
    }
}

package net.chrotos.chrotoscloud.paper;

import com.google.inject.Inject;
import net.chrotos.chrotoscloud.tasks.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;

public class PaperScheduler implements Scheduler {
    private final PaperCloud cloud;
    private BukkitTask cleanupTask;
    private final CopyOnWriteArrayList<BukkitTask> tasks = new CopyOnWriteArrayList<>();

    @Inject
    public PaperScheduler(PaperCloud cloud) {
        this.cloud = cloud;
    }

    protected void initialize() {
        cleanupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(cloud.getPlugin(), () ->
                tasks.removeIf(BukkitTask::isCancelled), 40L, 40L);
    }

    @Override
    public void runTask(Runnable runnable) {
        tasks.add(Bukkit.getScheduler().runTask(cloud.getPlugin(), runnable));
    }

    @Override
    public void runTaskLater(Runnable runnable, Duration delay) {
        tasks.add(Bukkit.getScheduler().runTaskLater(cloud.getPlugin(), runnable, delay.toSeconds() * 20));
    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        tasks.add(Bukkit.getScheduler().runTaskAsynchronously(cloud.getPlugin(), runnable));
    }

    @Override
    public void runTaskLaterAsync(Runnable runnable, Duration delay) {
        tasks.add(Bukkit.getScheduler().runTaskLaterAsynchronously(cloud.getPlugin(), runnable, delay.toSeconds() * 20));
    }

    @Override
    public void runTaskTimer(Runnable runnable, Duration delay, Duration period) {
        tasks.add(Bukkit.getScheduler().runTaskTimer(cloud.getPlugin(), runnable, delay.toSeconds() * 20, period.toSeconds() * 20));
    }

    @Override
    public void runTaskTimerAsync(Runnable runnable, Duration delay, Duration period) {
        tasks.add(Bukkit.getScheduler().runTaskTimerAsynchronously(cloud.getPlugin(), runnable, delay.toSeconds() * 20, delay.toSeconds() * 20));
    }

    @Override
    public void close() {
        if (cleanupTask != null && !cleanupTask.isCancelled()) {
            cleanupTask.cancel();
        }

        tasks.removeIf(BukkitTask::isCancelled);
        tasks.forEach(BukkitTask::cancel);
    }
}

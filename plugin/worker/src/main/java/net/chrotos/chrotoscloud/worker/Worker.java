package net.chrotos.chrotoscloud.worker;

import net.chrotos.chrotoscloud.cache.Lock;
import net.chrotos.chrotoscloud.jobs.AbstractJob;
import net.chrotos.chrotoscloud.jobs.JobState;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.Duration;
import java.util.Calendar;
import java.util.Properties;

public class Worker {
    private final Scheduler scheduler;
    private final WorkerCloud cloud;

    protected Worker(WorkerCloud cloud) throws SchedulerException {
        Properties properties = new Properties();
        properties.put("org.quartz.threadPool.threadCount", "5");

        scheduler = new StdSchedulerFactory(properties).getScheduler();
        this.cloud = cloud;
    }

    protected void start() throws SchedulerException, InterruptedException {
        scheduler.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        cloud.getScheduler().runTaskTimerAsync(this::pullJobs, Duration.ofSeconds(5), Duration.ofSeconds(5));
        cloud.getScheduler().runTaskTimerAsync(this::refreshLocks, Duration.ofSeconds(5), Duration.ofSeconds(5));

        Thread.currentThread().join();
    }

    private void shutdown() {
        try {
            if (!scheduler.isStarted() || scheduler.isShutdown()) {
                return;
            }

            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshLocks() {
        try {
            scheduler.getCurrentlyExecutingJobs().forEach(jobExecutionContext -> {
                try {
                    ((Lock) jobExecutionContext.getJobDetail().getJobDataMap().get("lock")).extend(Duration.ofSeconds(10));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pullJobs() {
        cloud.getCache().keys("jobs:*").forEach(jobKey -> {
            Lock lock = cloud.getCache().acquireLockOrNull(jobKey + ":lock", Duration.ofSeconds(10));
            if (lock == null) {
                return;
            }
            AbstractJob job = cloud.getJobManager().getJobByKey(jobKey);
            Calendar now = Calendar.getInstance();
            if (job.getState() != JobState.QUEUED) {
                return;
            }

            if (job.notBefore() != null && job.notBefore().after(now)) {
                lock.release();
                return;
            }

            if (job.notAfter() != null && job.notAfter().before(now)) {
                lock.release();
                cloud.getCache().set(jobKey, null);
                return;
            }

            try {
                scheduler.addJob(buildJob(job, lock), false);
            } catch (Exception e) {
                e.printStackTrace();
                cloud.getCache().set(jobKey, null);
                lock.release();
            }
        });
    }

    private JobDetail buildJob(AbstractJob abstractJob, Lock lock) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("job", abstractJob);
        jobDataMap.put("lock", lock);
        jobDataMap.put("attempt", 0);

        return JobBuilder.newJob(QuartzJob.class)
                .withIdentity(abstractJob.getUniqueId().toString())
                .usingJobData(jobDataMap)
                .build();
    }
}

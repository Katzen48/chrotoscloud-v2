package net.chrotos.chrotoscloud.worker;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.cache.Lock;
import net.chrotos.chrotoscloud.jobs.AbstractJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.Duration;

public class QuartzJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int attempt = context.getJobDetail().getJobDataMap().getIntValue("attempt") + 1;
        context.put("attempt", attempt);

        AbstractJob job = (AbstractJob) context.get("job");
        Lock lock = (Lock) context.get("lock");

        try {
            if (job.requiresTransaction()) {
                Cloud.getInstance().getPersistence().runInTransaction((job::handle));
            } else {
                job.handle(null);
            }

            lock.release();
            Cloud.getInstance().getCache().set("jobs:" + job, null);
        } catch (Exception e) {
            if (job.getRetries() > attempt) {
                throw new JobExecutionException(e, true);
            } else {
                throw new JobExecutionException(e);
            }
        }
    }
}

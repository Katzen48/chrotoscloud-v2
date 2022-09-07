package net.chrotos.chrotoscloud.jobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;

import java.time.Duration;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CloudJobManager implements JobManager {
    private final Cloud cloud;
    private static final String CACHE_KEY = "jobs:%s";
    private static final Gson gson;

    @Override
    public void initialize() {}

    @Override
    public void queue(@NonNull AbstractJob job) {
        if (!job.isAsync()) {
            Calendar now = Calendar.getInstance();

            Duration delay = job.notBefore() == null || job.notBefore().before(now) ? Duration.ZERO :
                    Duration.between(now.toInstant(), job.notBefore().toInstant());

            scheduleJobInternally(job, delay, 0);
            return;
        }

        String cacheKey = String.format(CACHE_KEY, job.getUniqueId());
        if (cloud.getCache().exists(cacheKey)) {
            throw new JobAlreadyScheduledException(job.getUniqueId());
        }

        cloud.getCache().set("jobs:" + job.getUniqueId(), gson.toJson(job));
    }

    private void scheduleJobInternally(AbstractJob job, Duration delay, int attempt) {
        cloud.getScheduler().runTaskLater(() -> {
            int thisAttempt = attempt + 1;

            try {
                if (job.requiresTransaction()) {
                    cloud.getPersistence().runInTransaction(job::handle);
                } else {
                    job.handle(null);
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (job.getRetries() > thisAttempt) {
                    scheduleJobInternally(job, Duration.ZERO, thisAttempt);
                }
            }
        }, delay);
    }

    @Override
    public AbstractJob getJob(@NonNull UUID uniqueId) {
        return getJobByKey(String.format(CACHE_KEY, uniqueId));
    }

    @Override
    public Set<AbstractJob> getJobs() {
        HashSet<AbstractJob> jobs = new HashSet<>();
        cloud.getCache().keys(String.format(CACHE_KEY, "*")).forEach(jobKey -> jobs.add(getJobByKey(jobKey)));

        return jobs;
    }

    public AbstractJob getJobByKey(@NonNull String key) {
        return gson.fromJson(cloud.getCache().get(key), AbstractJob.class);
    }

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(AbstractJob.class, new JobSerializer())
                .create();
    }
}

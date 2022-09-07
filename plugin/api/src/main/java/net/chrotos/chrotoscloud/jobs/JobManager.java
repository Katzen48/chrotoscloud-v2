package net.chrotos.chrotoscloud.jobs;

import lombok.NonNull;

import java.util.Set;
import java.util.UUID;

public interface JobManager {
    void initialize();
    void queue(@NonNull AbstractJob job);
    AbstractJob getJob(@NonNull UUID uuid);
    Set<AbstractJob> getJobs();
}

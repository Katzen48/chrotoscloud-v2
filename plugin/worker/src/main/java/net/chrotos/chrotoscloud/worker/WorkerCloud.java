package net.chrotos.chrotoscloud.worker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.cache.Lock;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.tasks.Scheduler;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

public class WorkerCloud extends CoreCloud {
    @Getter
    private final Injector serviceInjector;
    @Getter
    private final WorkerScheduler scheduler;

    public WorkerCloud() {
        this.serviceInjector = Guice.createInjector(new WorkerModule(this));
        this.scheduler = getServiceInjector().getInstance(WorkerScheduler.class);
        setCloudConfig(new WorkerConfig());
    }

    @Override
    public @NonNull String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NonNull GameManager getGameManager() {
        return null;
    }

    @Override
    public File getTranslationDir() {
        return null;
    }
}

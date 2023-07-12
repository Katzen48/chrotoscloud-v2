package net.chrotos.chrotoscloud.worker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.games.GameManager;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WorkerCloud extends CoreCloud {
    @Getter
    private final WorkerScheduler scheduler;

    public WorkerCloud() {
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
    public File getTranslationDir() {
        return null;
    }
}

package net.chrotos.chrotoscloud.persistence.mysql;

import com.google.inject.Injector;
import lombok.NonNull;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.tasks.Scheduler;

import java.io.File;

public class MockCloud extends CoreCloud {

    public MockCloud() {
        setCloudConfig(new MockConfig());
    }

    @Override
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }

    @Override
    public File getTranslationDir() {
        return null;
    }

    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    protected boolean shouldLoadCache() {
        return false;
    }

    @Override
    protected boolean shouldLoadPubSub() {
        return false;
    }
}

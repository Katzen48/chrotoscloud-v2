package net.chrotos.chrotoscloud.persistence.mysql;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.player.Player;
import net.chrotos.chrotoscloud.tasks.Scheduler;

import java.io.File;
import java.util.List;

public class MockCloud extends CoreCloud {
    private final Injector serviceInjector;
    public MockCloud() {
        this.serviceInjector = Guice.createInjector(new MockModule(this));
        setCloudConfig(new MockConfig());
    }

    @Override
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }

    @Override
    public GameManager getGameManager() {
        return null;
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

    @Override
    protected boolean shouldLoadQueue() {
        return false;
    }

    @NonNull
    @Override
    public Injector getServiceInjector() {
        return this.serviceInjector;
    }
}

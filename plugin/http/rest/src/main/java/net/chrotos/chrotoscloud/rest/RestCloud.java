package net.chrotos.chrotoscloud.rest;

import lombok.NonNull;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.tasks.Scheduler;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RestCloud extends CoreCloud {

    public RestCloud() {
        setCloudConfig(new RestConfig());
    }

    @Override
    public void initialize() {
        super.initialize();
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

    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    protected boolean shouldLoadPubSub() {
        return false;
    }

    @Override
    protected boolean shouldLoadCache() {
        return false;
    }
}

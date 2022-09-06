package net.chrotos.chrotoscloud.rest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.games.GameManager;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RestCloud extends CoreCloud {
    @Getter
    private Injector serviceInjector;

    public RestCloud() {
        this.serviceInjector = Guice.createInjector(new RestModule(this));
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
    public @NonNull GameManager getGameManager() {
        return null;
    }

    @Override
    public File getTranslationDir() {
        return null;
    }

    @Override
    protected boolean shouldLoadPubSub() {
        return false;
    }

    @Override
    protected boolean shouldLoadQueue() {
        return false;
    }

    @Override
    protected boolean shouldLoadCache() {
        return false;
    }
}

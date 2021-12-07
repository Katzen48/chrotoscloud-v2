package net.chrotos.chrotoscloud;

import lombok.Getter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;

import java.util.Iterator;
import java.util.ServiceLoader;

public abstract class CoreCloud extends Cloud {
    @Getter
    private boolean loaded;
    @Getter
    private boolean initialized;

    public void load() {
        if (loaded) {
            return;
        }

        loadServices();

        this.loaded = true;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        if (!loaded) {
            throw new IllegalArgumentException("Not loaded! Cannot initialize!");
        }

        this.persistence.configure(getCloudConfig());

        this.initialized = true;
    }

    private void loadServices() {
        this.persistence = loadService(PersistenceAdapter.class);
    }

    private <E> E loadService(Class<E> clazz) {
        ServiceLoader<E> serviceLoader = ServiceLoader.load(clazz);

        Iterator<E> iterator = serviceLoader.iterator();

        if (!iterator.hasNext()) {
            throw new IllegalStateException(clazz.getSimpleName() + " has no implementation!");
        }

        E service = iterator.next();

        if (iterator.hasNext()) {
            throw new IllegalStateException("Multiple Implementations found for " + clazz.getSimpleName() + "! Cannot" +
                                            "decide, which to use");
        }

        return service;
    }

    protected void setCloudConfig(CloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }
}

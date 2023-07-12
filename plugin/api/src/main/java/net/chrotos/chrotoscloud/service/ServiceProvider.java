package net.chrotos.chrotoscloud.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.cache.CacheAdapter;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.jobs.JobManager;
import net.chrotos.chrotoscloud.messaging.pubsub.PubSubAdapter;

public abstract class ServiceProvider extends AbstractModule {
    /**
     * Called first
     */
    public void load(ServiceContainer container) {

    }

    /**
     * Called last
     */
    public void boot(ServiceContainer container) {

    }

    /**
     * Called after load
     */
    public void initialize(ServiceContainer container) {

    }
}

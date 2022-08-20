package net.chrotos.chrotoscloud;

import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.cache.RedisCacheAdapter;
import net.chrotos.chrotoscloud.chat.ChatManager;
import net.chrotos.chrotoscloud.chat.CoreChatManager;
import net.chrotos.chrotoscloud.messaging.queue.RabbitQueueAdapter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.player.CloudPlayerManager;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.TimeZone;

public abstract class CoreCloud extends Cloud {
    private static boolean loaded;
    private static boolean initialized;
    @Getter
    @NonNull
    private final CloudPlayerManager playerManager;
    @Getter
    @NonNull
    private final ChatManager chatManager;

    protected CoreCloud() {
        this.playerManager = new CloudPlayerManager(this);
        this.chatManager = new CoreChatManager();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public void load() {
        if (loaded) {
            return;
        }

        loadServices();

        if (shouldLoadQueue()) {
            RabbitQueueAdapter queueAdapter = new RabbitQueueAdapter();
            this.queue = queueAdapter;
            queueAdapter.configure(getCloudConfig());
        }

        loaded = true;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        if (!loaded) {
            throw new IllegalArgumentException("Not loaded! Cannot initialize!");
        }

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        RedisCacheAdapter redisAdapter = null;
        if (shouldLoadCache()) {
            redisAdapter = new RedisCacheAdapter();
            this.cache = redisAdapter;
            this.cache.configure(getCloudConfig());
        }

        if (shouldLoadPubSub()) {
            this.pubSub = redisAdapter.getPubSub();
        }

        Thread.currentThread().setContextClassLoader(getServiceClassLoader());
        this.persistence.configure(getCloudConfig());

        if (shouldLoadQueue()) {
            queue.initialize();
        }

        Thread.currentThread().setContextClassLoader(loader);

        initialized = true;
    }

    private void loadServices() {
        this.persistence = loadService(PersistenceAdapter.class);
    }

    private <E> E loadService(Class<E> clazz) {
        ServiceLoader<E> serviceLoader = ServiceLoader.load(clazz, getServiceClassLoader());

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

    protected boolean shouldLoadQueue() {
        return true;
    }

    protected boolean shouldLoadPubSub() {
        return true;
    }

    protected boolean shouldLoadCache() {
        return true;
    }

    protected void setCloudConfig(CloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }
}

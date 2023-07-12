package net.chrotos.chrotoscloud;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.chrotos.chrotoscloud.cache.CacheAdapter;
import net.chrotos.chrotoscloud.chat.ChatManager;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.jobs.JobManager;
import net.chrotos.chrotoscloud.messaging.pubsub.PubSubAdapter;
import net.chrotos.chrotoscloud.messaging.queue.QueueAdapter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.player.PlayerManager;
import net.chrotos.chrotoscloud.service.ServiceContainer;
import net.chrotos.chrotoscloud.tasks.Scheduler;
import net.kyori.adventure.translation.TranslationRegistry;

import java.io.File;
import java.util.Iterator;
import java.util.ServiceLoader;

@Getter
public abstract class Cloud implements ServiceContainer {
    private static Cloud instance;
    @Setter
    private static ClassLoader serviceClassLoader;
    protected CloudConfig cloudConfig;
    protected CacheAdapter cache;
    protected PubSubAdapter pubSub;
    protected TranslationRegistry translationRegistry;

    @NonNull
    public static Cloud getInstance() {
        if (Cloud.instance == null) {
            // TODO refactor to service provider
            ServiceLoader<Cloud> serviceLoader = ServiceLoader.load(Cloud.class, getServiceClassLoader());

            Iterator<Cloud> iterator = serviceLoader.iterator();

            if (!iterator.hasNext()) {
                throw new IllegalArgumentException("No Implementation found!");
            }

            instance = iterator.next();
        }

        return instance;
    }

    @NonNull
    public static ClassLoader getServiceClassLoader() {
        return serviceClassLoader != null ? serviceClassLoader : Thread.currentThread().getContextClassLoader();
    }

    // Management stuff (loading, initialization)

    /**
     * Load the module with it's services, before it can be initialized
     * @throws IllegalStateException if the a service is missing it's implementing module
     */
    public abstract void load();

    /**
     * Initialize the module, after it has been loaded
     * @throws IllegalStateException if the module has not been loaded
     */
    public abstract void initialize();

    public abstract boolean isInitialized();

    public abstract boolean isLoaded();

    // Getter
    @NonNull
    public final PlayerManager getPlayerManager() {
        return getServiceInjector().getInstance(PlayerManager.class);
    }

    @NonNull
    public final ChatManager getChatManager() {
        return getServiceInjector().getInstance(ChatManager.class);
    }

    public final QueueAdapter getQueue() {
        return getServiceInjector().getInstance(QueueAdapter.class);
    }

    @NonNull
    public abstract String getHostname();

    public final GameManager getGameManager() {
        return getServiceInjector().getInstance(GameManager.class);
    }
    public abstract File getTranslationDir();
    public abstract Scheduler getScheduler();
    public abstract JobManager getJobManager();

    public PersistenceAdapter getPersistence() {
        return getServiceInjector().getInstance(PersistenceAdapter.class);
    }

    public String getGameMode() {
        return getCloudConfig().getGameMode();
    }
}

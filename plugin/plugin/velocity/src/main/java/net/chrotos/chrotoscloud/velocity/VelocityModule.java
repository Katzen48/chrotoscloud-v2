package net.chrotos.chrotoscloud.velocity;

import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.cache.CacheAdapter;
import net.chrotos.chrotoscloud.chat.ChatManager;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.jobs.JobManager;
import net.chrotos.chrotoscloud.messaging.pubsub.PubSubAdapter;
import net.chrotos.chrotoscloud.messaging.queue.QueueAdapter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.player.PlayerManager;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import net.chrotos.chrotoscloud.velocity.player.VelocitySidedPlayerFactory;

@AllArgsConstructor
public class VelocityModule extends AbstractModule {
    private final VelocityCloud cloud;

    @Override
    protected void configure() {
        bind(Cloud.class).toInstance(cloud);
        bind(VelocityCloud.class).toInstance(cloud);
        bind(SidedPlayerFactory.class).toInstance(new VelocitySidedPlayerFactory(cloud));

        bind(PersistenceAdapter.class).toProvider(cloud::getPersistence);
        bind(QueueAdapter.class).toProvider(cloud::getQueue);
        bind(CacheAdapter.class).toProvider(cloud::getCache);
        bind(CloudConfig.class).toProvider(cloud::getCloudConfig);
        bind(GameManager.class).toProvider(cloud::getGameManager);
        bind(ChatManager.class).toProvider(cloud::getChatManager);
        bind(PlayerManager.class).toProvider(cloud::getPlayerManager);
        bind(PubSubAdapter.class).toProvider(cloud::getPubSub);
        bind(JobManager.class).toProvider(cloud::getJobManager);
    }
}

package net.chrotos.chrotoscloud.persistence.mysql;

import com.google.inject.AbstractModule;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.cache.CacheAdapter;
import net.chrotos.chrotoscloud.chat.ChatManager;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.messaging.pubsub.PubSubAdapter;
import net.chrotos.chrotoscloud.messaging.queue.QueueAdapter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.player.PlayerManager;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;

public class MockModule extends AbstractModule {
    private MockCloud cloud;

    protected MockModule(MockCloud cloud) {
        this.cloud = cloud;
    }

    @Override
    protected void configure() {
        bind(Cloud.class).toInstance(cloud);
        bind(MockCloud.class).toInstance(cloud);
        bind(SidedPlayerFactory.class).to(MockSidedPlayerFactory.class);

        bind(PersistenceAdapter.class).toProvider(cloud::getPersistence);
        bind(QueueAdapter.class).toProvider(cloud::getQueue);
        bind(CacheAdapter.class).toProvider(cloud::getCache);
        bind(CloudConfig.class).toProvider(cloud::getCloudConfig);
        bind(GameManager.class).toProvider(cloud::getGameManager);
        bind(ChatManager.class).toProvider(cloud::getChatManager);
        bind(PlayerManager.class).toProvider(cloud::getPlayerManager);
        bind(PubSubAdapter.class).toProvider(cloud::getPubSub);
    }
}

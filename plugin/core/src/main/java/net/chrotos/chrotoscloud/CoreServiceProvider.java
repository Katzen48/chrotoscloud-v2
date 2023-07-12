package net.chrotos.chrotoscloud;

import com.google.auto.service.AutoService;
import com.google.inject.Singleton;
import net.chrotos.chrotoscloud.cache.CacheAdapter;
import net.chrotos.chrotoscloud.chat.ChatManager;
import net.chrotos.chrotoscloud.chat.CoreChatManager;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.jobs.JobManager;
import net.chrotos.chrotoscloud.messaging.pubsub.PubSubAdapter;
import net.chrotos.chrotoscloud.messaging.queue.QueueAdapter;
import net.chrotos.chrotoscloud.messaging.queue.RabbitQueueAdapter;
import net.chrotos.chrotoscloud.player.CloudPlayerManager;
import net.chrotos.chrotoscloud.player.PlayerManager;
import net.chrotos.chrotoscloud.service.ServiceProvider;

@AutoService(ServiceProvider.class)
public final class CoreServiceProvider extends ServiceProvider {
    @Override
    public void configure() {
        super.configure();

        bind(ChatManager.class).to(CoreChatManager.class).in(Singleton.class);
        bind(PlayerManager.class).to(CloudPlayerManager.class).in(Singleton.class);
        bind(QueueAdapter.class).to(RabbitQueueAdapter.class).in(Singleton.class);

        bind(Cloud.class).toProvider(Cloud::getInstance).in(Singleton.class);
        // TODO change to service provider
        bind(CacheAdapter.class).toProvider(() -> Cloud.getInstance().getCache()).in(Singleton.class);
        bind(CloudConfig.class).toProvider(() -> Cloud.getInstance().getCloudConfig()).in(Singleton.class);
        // TODO change to service provider
        bind(GameManager.class).toProvider(() -> Cloud.getInstance().getGameManager()).in(Singleton.class);
        // TODO change to service provider
        bind(PubSubAdapter.class).toProvider(() -> Cloud.getInstance().getPubSub()).in(Singleton.class);
        // TODO change to service provider
        bind(JobManager.class).toProvider(() -> Cloud.getInstance().getJobManager()).in(Singleton.class);
    }
}

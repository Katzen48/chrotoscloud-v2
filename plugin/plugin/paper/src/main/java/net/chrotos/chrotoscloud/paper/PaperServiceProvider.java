package net.chrotos.chrotoscloud.paper;

import com.google.auto.service.AutoService;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.messaging.queue.QueueAdapter;
import net.chrotos.chrotoscloud.paper.games.PaperGameManager;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import net.chrotos.chrotoscloud.service.ServiceContainer;
import net.chrotos.chrotoscloud.service.ServiceProvider;

import java.io.IOException;

@AutoService(ServiceProvider.class)
public class PaperServiceProvider extends ServiceProvider {
    @Override
    public void configure() {
        super.configure();

        bind(SidedPlayerFactory.class).to(PaperSidedPlayerFactory.class).in(Singleton.class);
        bind(GameManager.class).to(PaperGameManager.class).in(Singleton.class);
    }

    @Override
    public void load(ServiceContainer container) {
        super.load(container);

        container.getServiceInjector().getInstance(QueueAdapter.class).configure(container.getCloudConfig());
    }

    @Override
    public void initialize(ServiceContainer container) {
        super.initialize(container);

        try {
            container.getServiceInjector().getInstance(GameManager.class).initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void boot(ServiceContainer container) {
        super.boot(container);

        container.getServiceInjector().getInstance(QueueAdapter.class).initialize();
    }

    @Provides
    @Singleton
    static PaperCloud getPaperCloud() {
        return (PaperCloud) Cloud.getInstance();
    }
}

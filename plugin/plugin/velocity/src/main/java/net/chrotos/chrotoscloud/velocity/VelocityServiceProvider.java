package net.chrotos.chrotoscloud.velocity;

import com.google.auto.service.AutoService;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.messaging.queue.QueueAdapter;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import net.chrotos.chrotoscloud.service.ServiceContainer;
import net.chrotos.chrotoscloud.service.ServiceProvider;
import net.chrotos.chrotoscloud.velocity.games.VelocityGameManager;
import net.chrotos.chrotoscloud.velocity.player.VelocitySidedPlayerFactory;

import java.io.IOException;

@AutoService(ServiceProvider.class)
public class VelocityServiceProvider extends ServiceProvider {
    @Override
    public void configure() {
        bind(SidedPlayerFactory.class).toProvider(() -> new VelocitySidedPlayerFactory(getVelocityCloud())).in(Singleton.class);
        bind(GameManager.class).to(VelocityGameManager.class).in(Singleton.class);
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
    static VelocityCloud getVelocityCloud() {
        return (VelocityCloud) Cloud.getInstance();
    }
}

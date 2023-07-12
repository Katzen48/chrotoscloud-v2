package net.chrotos.chrotoscloud.worker;

import com.google.auto.service.AutoService;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.messaging.queue.QueueAdapter;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import net.chrotos.chrotoscloud.service.ServiceContainer;
import net.chrotos.chrotoscloud.service.ServiceProvider;

@AutoService(ServiceProvider.class)
public class WorkerServiceProvider extends ServiceProvider {
    @Override
    public void configure() {
        super.configure();

        bind(SidedPlayerFactory.class).to(WorkerSidedPlayerFactory.class).in(Singleton.class);
    }

    @Override
    public void load(ServiceContainer container) {
        super.load(container);

        container.getServiceInjector().getInstance(QueueAdapter.class).configure(container.getCloudConfig());
    }

    @Override
    public void boot(ServiceContainer container) {
        super.boot(container);

        container.getServiceInjector().getInstance(QueueAdapter.class).initialize();
    }

    @Provides
    @Singleton
    static WorkerCloud getWorkerCloud() {
        return (WorkerCloud) Cloud.getInstance();
    }
}

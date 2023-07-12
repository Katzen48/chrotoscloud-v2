package net.chrotos.chrotoscloud.persistence.mysql;

import com.google.auto.service.AutoService;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.service.ServiceContainer;
import net.chrotos.chrotoscloud.service.ServiceProvider;

@AutoService(ServiceProvider.class)
public class MysqlPersistenceServiceProvider extends ServiceProvider {
    @Override
    public void configure() {
        super.configure();

        bind(PersistenceAdapter.class).toInstance(new MysqlPersistenceAdapter());
    }

    @Override
    public void load(ServiceContainer container) {
        container.getServiceInjector().getInstance(PersistenceAdapter.class).configure(container.getCloudConfig());
    }
}

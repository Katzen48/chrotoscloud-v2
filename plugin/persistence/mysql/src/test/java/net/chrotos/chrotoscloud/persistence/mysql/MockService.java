package net.chrotos.chrotoscloud.persistence.mysql;

import com.google.auto.service.AutoService;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import net.chrotos.chrotoscloud.service.ServiceProvider;

@AutoService(ServiceProvider.class)
public class MockService extends ServiceProvider {
    @Override
    public void configure() {
        super.configure();

        bind(SidedPlayerFactory.class).to(MockSidedPlayerFactory.class);
    }
}

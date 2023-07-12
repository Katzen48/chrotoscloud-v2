package net.chrotos.chrotoscloud.rest;

import com.google.auto.service.AutoService;
import jakarta.inject.Singleton;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import net.chrotos.chrotoscloud.service.ServiceProvider;

@AutoService(ServiceProvider.class)
public class RestServiceProvider extends ServiceProvider {
    @Override
    public void configure() {
        bind(SidedPlayerFactory.class).to(RestSidedPlayerFactory.class).in(Singleton.class);
    }
}

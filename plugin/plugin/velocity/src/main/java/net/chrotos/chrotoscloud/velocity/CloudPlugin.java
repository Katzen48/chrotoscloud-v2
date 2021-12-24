package net.chrotos.chrotoscloud.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CoreCloud;
import org.slf4j.Logger;

@Plugin(id="chrotoscloud", name = "ChrotosCloud", version = "3.0-SNAPSHOT", authors = {"Katzen48"})
public class CloudPlugin {
    private final ProxyServer proxyServer;
    private final Logger logger;
    private VelocityCloud cloud;

    @Inject
    public CloudPlugin(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.cloud = (VelocityCloud) Cloud.getInstance();

        cloud.load();
        cloud.initialize();
    }
}

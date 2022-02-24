package net.chrotos.chrotoscloud.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.velocity.commands.HubCommand;
import net.chrotos.chrotoscloud.velocity.player.PermissionsProvider;
import org.slf4j.Logger;

@Plugin(id="chrotoscloud", name = "ChrotosCloud", version = "3.0-SNAPSHOT", authors = {"Katzen48"})
public class CloudPlugin {
    protected final ProxyServer proxyServer;
    private final Logger logger;
    protected final VelocityCloud cloud;
    private VelocityCacheSynchronizer synchronizer;

    @Inject
    public CloudPlugin(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;

        Cloud.setServiceClassLoader(getClass().getClassLoader());
        this.cloud = (VelocityCloud) Cloud.getInstance();
    }

    @Subscribe(async = false)
    public void onProxyInitialization(ProxyInitializeEvent event) {
        cloud.load();
        cloud.initialize();

        proxyServer.getEventManager().register(this, new VelocityEventHandler(this));

        synchronizer = new VelocityCacheSynchronizer(this);
        synchronizer.initialize();
        proxyServer.getEventManager().register(this, synchronizer);

        // Reload function is incompatible with the manipulations done by the plugin, so remove the whole command
        proxyServer.getCommandManager().unregister("velocity");

        registerCommands();
    }

    private void registerCommands() {
        CommandMeta lobbyMeta = proxyServer.getCommandManager()
                                            .metaBuilder("hub")
                                            .aliases("lobby", "back")
                                            .build();

        proxyServer.getCommandManager().register(lobbyMeta, new HubCommand(proxyServer));
    }

    @Subscribe(async = false)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        synchronizer.destruct();
    }
}

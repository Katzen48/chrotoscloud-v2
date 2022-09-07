package net.chrotos.chrotoscloud.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.velocity.commands.BanCommand;
import net.chrotos.chrotoscloud.velocity.commands.HubCommand;
import net.chrotos.chrotoscloud.velocity.commands.UnbanCommand;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id="chrotoscloud", name = "ChrotosCloud", version = "3.0-SNAPSHOT", authors = {"Katzen48"})
public class CloudPlugin {
    @Getter
    protected final ProxyServer proxyServer;
    private final Logger logger;
    protected final VelocityCloud cloud;
    @Getter
    private VelocityCacheSynchronizer synchronizer;

    @Inject
    public CloudPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDir) {
        this.proxyServer = proxyServer;
        this.logger = logger;

        Cloud.setServiceClassLoader(getClass().getClassLoader());
        this.cloud = (VelocityCloud) Cloud.getInstance();
        this.cloud.setProxyServer(proxyServer);
        this.cloud.setLogger(logger);
        this.cloud.setDataDir(dataDir);
        this.cloud.setPlugin(this);
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
        BanCommand.register(this);
        UnbanCommand.register(this);
    }

    @Subscribe(async = false)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        cloud.getScheduler().close();
        synchronizer.destruct();
    }
}

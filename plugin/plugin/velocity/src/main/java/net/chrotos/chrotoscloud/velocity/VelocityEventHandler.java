package net.chrotos.chrotoscloud.velocity;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.NonNull;
import net.chrotos.chrotoscloud.games.CloudGameServer;
import net.chrotos.chrotoscloud.games.events.GameServerConnectedEvent;
import net.chrotos.chrotoscloud.player.PlayerSoftDeletedException;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.chrotos.chrotoscloud.velocity.player.PermissionsProvider;
import net.chrotos.chrotoscloud.velocity.player.VelocitySidedPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.CompletableFuture;

public class VelocityEventHandler {
    private final CloudPlugin plugin;
    private final PermissionsProvider permissionsProvider;

    protected VelocityEventHandler(@NonNull CloudPlugin plugin) {
        this.plugin = plugin;
        this.permissionsProvider = new PermissionsProvider(plugin.cloud);
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        event.getPlayer().sendPlayerListHeaderAndFooter(plugin.proxyServer.getConfiguration().getMotd(), Component.empty());

        CompletableFuture.supplyAsync(() -> {
            CloudGameServer previousServer = event.getPreviousServer().isPresent() ?
                    plugin.cloud.getGameManager().getGameServer(event.getPreviousServer().get().getServerInfo().getName()).join() : null;

            plugin.cloud.getQueue().publish("games.server.connect:" + event.getPlayer().getCurrentServer().get()
                            .getServerInfo().getName(),
                    new GameServerConnectedEvent(previousServer, event.getPlayer().getUniqueId()));

            return true;
        });
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPermissionsSetup(PermissionsSetupEvent event, Continuation continuation) {
        if (!(event.getSubject() instanceof Player player)) {
            continuation.resume();
            return;
        }

        try {
            SidedPlayer sidedPlayer = new VelocitySidedPlayer(player);
            plugin.cloud.getPlayerManager().getOrCreatePlayer(sidedPlayer);

            event.setProvider(permissionsProvider);
            continuation.resume();
        } catch (PlayerSoftDeletedException e) {
            player.disconnect(Component.text("Your account has been deleted!").color(NamedTextColor.RED)); //TODO translate
        } catch (Exception e) {
            continuation.resumeWithException(e);
        }
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisconnect(DisconnectEvent event, Continuation continuation) {
        if (event.getPlayer() == null || event.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) {
            return;
        }

        try {
            plugin.cloud.getPlayerManager().logoutPlayer(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            continuation.resumeWithException(e);
        }
    }
}

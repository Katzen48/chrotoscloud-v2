package net.chrotos.chrotoscloud.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.NonNull;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.chrotos.chrotoscloud.velocity.player.PermissionsProvider;
import net.chrotos.chrotoscloud.velocity.player.VelocitySidedPlayer;

public class VelocityEventHandler {
    private final CloudPlugin plugin;
    private final PermissionsProvider permissionsProvider;

    protected VelocityEventHandler(@NonNull CloudPlugin plugin) {
        this.plugin = plugin;
        this.permissionsProvider = new PermissionsProvider(plugin.cloud);
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        SidedPlayer sidedPlayer = new VelocitySidedPlayer(event.getPlayer());
        plugin.cloud.getPlayerManager().getOrCreatePlayer(sidedPlayer);
    }

    @Subscribe
    public void onPermissionsSetup(PermissionsSetupEvent event) {
        if (!(event.getSubject() instanceof Player)) {
            return;
        }

        event.setProvider(permissionsProvider);
    }
}

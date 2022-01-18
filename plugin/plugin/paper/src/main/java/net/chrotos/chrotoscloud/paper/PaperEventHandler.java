package net.chrotos.chrotoscloud.paper;

import com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.paper.permissions.PermissibleInjector;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PaperEventHandler implements Listener {
    private final PaperCloud cloud;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        try {
            cloud.getPlayerManager().getOrCreatePlayer(event.getPlayer().getUniqueId(),
                                                        event.getPlayer().getName());
            PermissibleInjector.inject(player, cloud);
        } catch (Exception e) {
            e.printStackTrace();
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("An error occured!")); // TODO: Translate with player.locale()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProfileWhitelistVerify(ProfileWhitelistVerifyEvent event) {
        try {
            net.chrotos.chrotoscloud.player.Player player = cloud.getPlayerManager().getOrCreatePlayer(event.getPlayerProfile().getId(),
                                                       event.getPlayerProfile().getName());

            event.setWhitelisted(event.isWhitelisted() || event.isOp() || player.hasPermission("minecraft.command.op")); // TODO: remove op?
        } catch (Exception e) {
            e.printStackTrace();
            event.setWhitelisted(false);
            event.kickMessage(Component.text("An error occured!")); // TODO: Translate with player.locale()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            cloud.getPlayerManager().logoutPlayer(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

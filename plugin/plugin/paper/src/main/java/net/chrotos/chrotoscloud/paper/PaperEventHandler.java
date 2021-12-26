package net.chrotos.chrotoscloud.paper;

import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.paper.permissions.PermissibleInjector;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@RequiredArgsConstructor
public class PaperEventHandler implements Listener {
    private final PaperCloud cloud;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        try {
            PermissibleInjector.inject(player, cloud);
        } catch (Exception e) {
            e.printStackTrace();
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("An error occured!")); // TODO: Translate with player.locale()
        }
    }
}

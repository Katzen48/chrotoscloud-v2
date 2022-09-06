package net.chrotos.chrotoscloud.paper;

import com.google.inject.Singleton;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Singleton
public class PaperSidedPlayerFactory implements SidedPlayerFactory {
    @Override
    public SidedPlayer generateSidedPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return null;
        }

        return generateSidedPlayer(player);
    }

    @Override
    public SidedPlayer generateSidedPlayer(Object sidedObject) {
        return new PaperSidedPlayer((Player) sidedObject);
    }
}

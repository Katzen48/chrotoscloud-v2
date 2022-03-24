package net.chrotos.chrotoscloud.games;

import lombok.NonNull;
import net.chrotos.chrotoscloud.player.Player;

import java.util.Collection;

public interface GameServer {
    String getName();
    int getMaxPlayers();
    int getPlayerCount();
    // TODO Future as return value
    void teleport(@NonNull Player player);
    void teleport(@NonNull Collection<Player> players);
    String getGameMode();
}

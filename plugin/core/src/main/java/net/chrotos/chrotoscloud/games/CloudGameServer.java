package net.chrotos.chrotoscloud.games;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.player.Player;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class CloudGameServer implements GameServer {
    private final String name;
    private final int maxPlayers;
    private final int playerCount;
    private final String gameMode;

    @Override
    public void teleport(@NonNull Player player) {
        Cloud.getInstance().getGameManager().requestTeleport(this, player);
    }

    @Override
    public void teleport(@NonNull Collection<Player> players) {
        players.forEach(this::teleport);
    }
}

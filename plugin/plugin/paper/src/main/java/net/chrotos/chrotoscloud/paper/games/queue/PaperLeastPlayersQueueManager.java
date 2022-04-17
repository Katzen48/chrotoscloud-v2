package net.chrotos.chrotoscloud.paper.games.queue;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.games.GameServer;
import net.chrotos.chrotoscloud.games.QueueManager;
import net.chrotos.chrotoscloud.games.QueueMode;
import net.chrotos.chrotoscloud.player.Player;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class PaperLeastPlayersQueueManager implements QueueManager {
    @Getter
    @NonNull
    private final String gameMode;

    @NonNull
    private final GameManager gameManager;

    @Override
    public @NonNull QueueMode getQueueMode() {
        return QueueMode.LEAST_PLAYERS;
    }

    @Override
    public CompletableFuture<? extends GameServer> getServer(@NonNull Player player) {
        return gameManager.getGameServers(getGameMode()).thenApply(gameServers ->
                gameServers.stream().filter(server -> server.getMaxPlayers() == 0 || server.getPlayerCount() < server.getMaxPlayers() - 2)
                        .min((s1, s2) -> Integer.compare(s2.getPlayerCount(), s1.getPlayerCount())).orElse(null));
    }
}

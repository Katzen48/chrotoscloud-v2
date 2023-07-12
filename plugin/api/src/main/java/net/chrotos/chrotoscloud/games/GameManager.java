package net.chrotos.chrotoscloud.games;

import lombok.NonNull;
import net.chrotos.chrotoscloud.games.gamemode.GameModeManager;
import net.chrotos.chrotoscloud.player.Player;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameManager {
    void initialize() throws IOException;
    GameModeManager getGameModeManager();
    CompletableFuture<? extends GameServer> getGameServer(@NonNull String name);
    CompletableFuture<List<? extends GameServer>> getGameServers();
    CompletableFuture<List<? extends GameServer>> getGameServers(@NonNull String gameMode);
    CompletableFuture<? extends GameServer> getRandom(@NonNull String gameMode);
    void requestTeleport(@NonNull GameServer server, @NonNull Player player);
    QueueManager getQueueManager(@NonNull QueueMode queueMode, @NonNull String gameMode);
}

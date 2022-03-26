package net.chrotos.chrotoscloud.games;

import lombok.NonNull;
import net.chrotos.chrotoscloud.player.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameManager {
    CompletableFuture<GameServer> getGameServer(@NonNull String name);
    CompletableFuture<List<GameServer>> getGameServers();
    CompletableFuture<List<GameServer>> getGameServers(@NonNull String gameMode);
    CompletableFuture<GameServer> getRandom(@NonNull String gameMode);
    void requestTeleport(@NonNull GameServer server, @NonNull Player player);
    QueueManager getQueueManager(@NonNull QueueMode queueMode, @NonNull String gameMode);
}

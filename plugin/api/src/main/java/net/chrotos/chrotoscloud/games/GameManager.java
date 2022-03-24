package net.chrotos.chrotoscloud.games;

import lombok.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameManager {
    CompletableFuture<GameServer> getGameServer(@NonNull String name);
    CompletableFuture<List<GameServer>> getGameServers();
    CompletableFuture<List<GameServer>> getGameServers(@NonNull String gameMode);
    CompletableFuture<GameServer> getRandom(@NonNull String gameMode);
    QueueManager getQueueManager(@NonNull QueueMode queueMode, @NonNull String gameMode);
}

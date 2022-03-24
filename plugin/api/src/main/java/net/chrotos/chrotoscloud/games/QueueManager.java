package net.chrotos.chrotoscloud.games;

import lombok.NonNull;
import net.chrotos.chrotoscloud.player.Player;

import java.util.concurrent.CompletableFuture;

public interface QueueManager {
    @NonNull
    QueueMode getQueueMode();
    @NonNull
    String getGameMode();
    CompletableFuture<GameServer> getServer(@NonNull Player player);
    // TODO multi queue
}

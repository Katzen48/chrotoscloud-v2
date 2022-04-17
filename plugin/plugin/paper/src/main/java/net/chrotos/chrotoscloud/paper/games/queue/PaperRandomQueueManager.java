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
public class PaperRandomQueueManager implements QueueManager {
    @Getter
    @NonNull
    private final String gameMode;
    @NonNull
    private final GameManager gameManager;

    @Override
    public @NonNull QueueMode getQueueMode() {
        return QueueMode.RANDOM;
    }

    @Override
    public CompletableFuture<? extends GameServer> getServer(@NonNull Player player) {
        return gameManager.getRandom(getGameMode());
    }
}

package net.chrotos.chrotoscloud.games.states;

import lombok.NonNull;
import net.chrotos.chrotoscloud.player.Player;

import java.util.UUID;

public interface GameState {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Player getPlayer();
    @NonNull
    String getGameMode();
    @NonNull
    String getName();
    @NonNull
    String getState();
    void setState(@NonNull String state);
}

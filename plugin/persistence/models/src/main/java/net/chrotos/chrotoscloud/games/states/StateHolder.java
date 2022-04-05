package net.chrotos.chrotoscloud.games.states;

import lombok.NonNull;

import java.util.Set;
import java.util.UUID;

public interface StateHolder {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Set<GameState> getStates();
    @NonNull
    Set<GameState> getStates(@NonNull String gameMode);
}

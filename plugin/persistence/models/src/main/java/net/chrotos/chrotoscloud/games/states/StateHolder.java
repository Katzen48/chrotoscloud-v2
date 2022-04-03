package net.chrotos.chrotoscloud.games.states;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface StateHolder {
    @NonNull
    UUID getUniqueId();
    @NonNull
    List<GameState> getStates();
    @NonNull
    List<GameState> getStates(@NonNull String gameMode);
}

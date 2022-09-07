package net.chrotos.chrotoscloud.games.states;

import lombok.NonNull;
import net.chrotos.chrotoscloud.Model;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface StateHolder extends Model {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Set<GameState> getStates();
    @NonNull
    Collection<? extends GameState> getStates(@NonNull String gameMode);

    @NonNull
    Collection<? extends GameState> getStatesByName(@NonNull String name);
}

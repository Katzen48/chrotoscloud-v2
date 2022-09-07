package net.chrotos.chrotoscloud.games.stats;

import lombok.NonNull;
import net.chrotos.chrotoscloud.Model;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface StatsHolder extends Model {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Set<GameStatistic> getStats();
    @NonNull
    Collection<? extends GameStatistic> getStats(@NonNull String gameMode);
}

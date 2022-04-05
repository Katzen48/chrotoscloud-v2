package net.chrotos.chrotoscloud.games.stats;

import lombok.NonNull;

import java.util.Set;
import java.util.UUID;

public interface StatsHolder {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Set<GameStatistic> getStats();
    @NonNull
    Set<GameStatistic> getStats(@NonNull String gameMode);
}

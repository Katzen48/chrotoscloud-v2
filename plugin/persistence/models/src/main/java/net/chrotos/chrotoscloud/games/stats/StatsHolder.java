package net.chrotos.chrotoscloud.games.stats;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface StatsHolder {
    @NonNull
    UUID getUniqueId();
    @NonNull
    List<GameStatistic> getStats();
    @NonNull
    List<GameStatistic> getStats(@NonNull String gameMode);
}

package net.chrotos.chrotoscloud.games.stats;

import lombok.NonNull;
import net.chrotos.chrotoscloud.player.Player;

import java.util.UUID;

public interface GameStatistic {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Player getPlayer();
    @NonNull
    String getGameMode();
    @NonNull
    String getName();
    double getValue();
    void setValue(double value);
    void increment();
    void increment(double value);
    void decrement();
    void decrement(double value);
}

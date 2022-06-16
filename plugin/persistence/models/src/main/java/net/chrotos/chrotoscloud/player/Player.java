package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.chrotos.chrotoscloud.economy.AccountHolder;
import net.chrotos.chrotoscloud.games.states.StateHolder;
import net.chrotos.chrotoscloud.games.stats.StatsHolder;
import net.chrotos.chrotoscloud.permissions.Permissible;
import net.chrotos.chrotoscloud.permissions.Rank;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface Player extends AccountHolder, InventoryHolder, Permissible, StatsHolder, StateHolder {
    SidedPlayer getSidedPlayer();
    @NonNull
    String getName();
    @NonNull
    UUID getUniqueId();
    Rank getRank();
    void setRank(Rank rank);
    @NonNull
    Component getPrefixes();
    String getResourcePackHash();
    void setResourcePack(@NonNull String url);
    void setResourcePack(@NonNull String url, @NonNull String hash);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, @NonNull Component prompt);
    boolean hasResourcePackApplied(@NonNull String hash);
}

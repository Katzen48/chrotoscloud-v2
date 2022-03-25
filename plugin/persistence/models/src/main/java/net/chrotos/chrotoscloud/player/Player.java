package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.chrotos.chrotoscloud.economy.AccountHolder;
import net.chrotos.chrotoscloud.games.stats.StatsHolder;
import net.chrotos.chrotoscloud.permissions.Permissible;
import net.chrotos.chrotoscloud.permissions.Rank;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface Player extends AccountHolder, StatsHolder, InventoryHolder, Permissible {
    SidedPlayer getSidedPlayer();
    @NonNull
    String getName();
    @NonNull
    UUID getUniqueId();
    Rank getRank();
    void setRank(Rank rank);
    @NonNull
    Component getPrefixes();
}

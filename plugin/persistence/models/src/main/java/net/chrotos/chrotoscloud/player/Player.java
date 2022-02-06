package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.chrotos.chrotoscloud.economy.AccountHolder;
import net.chrotos.chrotoscloud.permissions.Permissible;
import net.chrotos.chrotoscloud.permissions.Rank;

import java.util.UUID;

public interface Player extends AccountHolder, Permissible {
    SidedPlayer getSidedPlayer();
    @NonNull
    String getName();
    @NonNull
    UUID getUniqueId();
    Rank getRank();
    void setRank(Rank rank);
    @NonNull
    String getPrefixes(); //TODO Change to kyori component
}

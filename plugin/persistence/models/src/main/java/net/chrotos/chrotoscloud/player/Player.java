package net.chrotos.chrotoscloud.player;

import net.chrotos.chrotoscloud.economy.AccountHolder;
import net.chrotos.chrotoscloud.permissions.Permissible;
import net.chrotos.chrotoscloud.permissions.Rank;

import java.util.UUID;

public interface Player extends AccountHolder, Permissible {
    SidedPlayer getSidedPlayer();
    String getName();
    UUID getUniqueId();
    Rank getRank();
    void setRank(Rank rank);
}

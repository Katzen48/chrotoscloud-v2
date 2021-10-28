package net.chrotos.chrotoscloud.player;

import net.chrotos.chrotoscloud.economy.AccountHolder;

import java.util.UUID;

public interface Player extends AccountHolder {
    SidedPlayer getSidedPlayer();
    String getName();
    UUID getUniqueId();
}

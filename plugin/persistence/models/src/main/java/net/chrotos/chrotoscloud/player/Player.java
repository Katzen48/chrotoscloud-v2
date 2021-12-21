package net.chrotos.chrotoscloud.player;

import net.chrotos.chrotoscloud.economy.AccountHolder;
import net.chrotos.chrotoscloud.permissions.Permissible;

import java.util.UUID;

public interface Player extends AccountHolder, Permissible {
    SidedPlayer getSidedPlayer();
    String getName();
    UUID getUniqueId();
}

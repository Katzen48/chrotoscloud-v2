package net.chrotos.chrotoscloud.player;

import lombok.NonNull;

import java.util.UUID;

public interface PlayerManager {
    Player getPlayer(@NonNull UUID uniqueId);
    Player getPlayer(@NonNull SidedPlayer sidedPlayer);
    Player getOrCreatePlayer(@NonNull UUID uniqueId, String name);
    Player getOrCreatePlayer(@NonNull SidedPlayer sidedPlayer);
    void logoutPlayer(@NonNull Player player);
    void logoutPlayer(@NonNull SidedPlayer player);
    void logoutPlayer(@NonNull UUID uniqueId);
}

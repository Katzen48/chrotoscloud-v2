package net.chrotos.chrotoscloud.player;

import lombok.NonNull;

import java.util.UUID;

public interface PlayerManager {
    Player getPlayer(@NonNull UUID uniqueId);
    Player getPlayer(@NonNull SidedPlayer sidedPlayer);
    Player getOrCreatePlayer(@NonNull Object sidedObject) throws PlayerSoftDeletedException;
    Player getOrCreatePlayer(@NonNull SidedPlayer sidedPlayer) throws PlayerSoftDeletedException;
    void logoutPlayer(@NonNull Player player);
    void logoutPlayer(@NonNull UUID uniqueId);
}

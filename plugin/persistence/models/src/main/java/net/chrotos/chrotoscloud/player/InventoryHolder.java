package net.chrotos.chrotoscloud.player;

import lombok.NonNull;

import java.util.Set;
import java.util.UUID;

public interface InventoryHolder {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Set<PlayerInventory> getInventories();
    PlayerInventory getInventory(@NonNull String gameMode);
}

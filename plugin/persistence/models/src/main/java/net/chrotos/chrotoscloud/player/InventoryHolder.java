package net.chrotos.chrotoscloud.player;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface InventoryHolder {
    @NonNull
    UUID getUniqueId();
    @NonNull
    List<PlayerInventory> getInventories();
    PlayerInventory getInventory(@NonNull String gameMode);
}

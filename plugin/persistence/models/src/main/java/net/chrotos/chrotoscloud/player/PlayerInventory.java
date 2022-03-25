package net.chrotos.chrotoscloud.player;

import lombok.NonNull;

import java.util.UUID;

public interface PlayerInventory {
    @NonNull
    UUID getUniqueId();
    @NonNull
    Player getPlayer();
    @NonNull
    String getGameMode();
    @NonNull
    String getContent(); // TODO change to POJO
    void setContent(@NonNull String content); // TODO change to POJO
}

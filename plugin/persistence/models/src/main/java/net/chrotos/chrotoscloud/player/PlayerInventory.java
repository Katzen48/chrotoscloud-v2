package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.chrotos.chrotoscloud.Model;

import java.util.UUID;

public interface PlayerInventory extends Model {
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

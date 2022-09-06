package net.chrotos.chrotoscloud.player;

import java.util.UUID;

public interface SidedPlayerFactory {
    SidedPlayer generateSidedPlayer(UUID uuid);
    SidedPlayer generateSidedPlayer(Object sidedObject);
}

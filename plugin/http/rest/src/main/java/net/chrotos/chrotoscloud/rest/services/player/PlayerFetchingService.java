package net.chrotos.chrotoscloud.rest.services.player;

import jakarta.ws.rs.NotFoundException;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.player.Player;

import java.util.UUID;

public abstract class PlayerFetchingService {
    public Player getPlayer(UUID uuid) {
        if (uuid == null) {
            throw new NotFoundException("UUID may not be null");
        }

        Player player = Cloud.getInstance().getPlayerManager().getPlayer(uuid);

        if (player == null) {
            throw new NotFoundException("Player not found");
        }

        return player;
    }
}

package net.chrotos.chrotoscloud.rest.services.player;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import net.chrotos.chrotoscloud.rest.exception.NotFoundException;

import java.util.UUID;

public abstract class PlayerFetchingService {
    public Player getPlayer(UUID uuid) {
        if (uuid == null) {
            throw new NotFoundException();
        }

        Player player = Cloud.getInstance().getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder().primaryKeyValue(uuid).build());

        if (player == null) {
            throw new NotFoundException("Player not found");
        }

        return player;
    }
}

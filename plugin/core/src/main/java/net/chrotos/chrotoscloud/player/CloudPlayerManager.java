package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.permissions.CloudRank;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.persistence.EntityExistsException;

import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
public class CloudPlayerManager implements PlayerManager {
    private final Cloud cloud;

    @Override
    public Player getPlayer(@NonNull UUID uniqueId) {
        return cloud.getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder()
                .primaryKeyValue(uniqueId).build());
    }

    @Override
    public Player getPlayer(@NonNull SidedPlayer sidedPlayer) {
        return getPlayer(sidedPlayer.getUniqueId());
    }

    @Override
    public Player getOrCreatePlayer(@NonNull UUID uniqueId, String name) throws PlayerSoftDeletedException {
        Player player = getPlayer(uniqueId);

        if (player != null) {
            return player;
        }

        if (name == null) {
            throw new IllegalArgumentException("'name' cannot be null");
        }

        try {
            return createPlayer(uniqueId, name);
        } catch (EntityExistsException e) {
            throw new PlayerSoftDeletedException(uniqueId);
        }
    }

    @Override
    public Player getOrCreatePlayer(@NonNull SidedPlayer sidedPlayer) throws PlayerSoftDeletedException {
        CloudPlayer player = (CloudPlayer) getOrCreatePlayer(sidedPlayer.getUniqueId(), sidedPlayer.getName());

        if (player != null) {
            player.setSidedPlayer(sidedPlayer);
        }

        return player;
    }

    @Override
    public void logoutPlayer(@NonNull Player player) {
        cloud.getPersistence().save(player);
        cloud.getPersistence().removeFromContext(player);
    }

    @Override
    public void logoutPlayer(@NonNull SidedPlayer player) {
        logoutPlayer(player.getUniqueId());
    }

    @Override
    public void logoutPlayer(@NonNull UUID uniqueId) {
        Player player = getPlayer(uniqueId);

        if (player == null) {
            return;
        }

        logoutPlayer(player);
    }

    private CloudPlayer createPlayer(@NonNull UUID uniqueId, @NonNull String name) {
        CloudPlayer player = new CloudPlayer(uniqueId, name);

        CloudRank defaultRank = cloud.getPersistence().getOne(CloudRank.class, DataSelectFilter.builder()
                .columnFilters(Collections.singletonMap("defaultRank", true))
                .build());
        player.setRank(defaultRank);

        cloud.getPersistence().save(player);

        return player;
    }
}

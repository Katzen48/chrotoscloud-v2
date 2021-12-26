package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;

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
    public Player getOrCreatePlayer(@NonNull UUID uniqueId, String name) {
        Player player = getPlayer(uniqueId);

        if (player != null) {
            return player;
        }

        if (name == null) {
            throw new IllegalArgumentException("'name' cannot be null");
        }

        return createPlayer(uniqueId, name);
    }

    @Override
    public Player getOrCreatePlayer(@NonNull SidedPlayer sidedPlayer) {
        CloudPlayer player = (CloudPlayer) getOrCreatePlayer(sidedPlayer.getUniqueId(), sidedPlayer.getName());

        if (player != null) {
            player.setSidedPlayer(sidedPlayer);
        }

        return player;
    }

    private CloudPlayer createPlayer(@NonNull UUID uniqueId, @NonNull String name) {
        CloudPlayer player = new CloudPlayer(uniqueId, name);
        cloud.getPersistence().save(player);

        return player;
    }
}

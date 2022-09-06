package net.chrotos.chrotoscloud.player;

import com.google.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.permissions.CloudRank;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.persistence.EntityExistsException;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CloudPlayerManager implements PlayerManager {
    private final Cloud cloud;
    private final SidedPlayerFactory sidedPlayerFactory;

    @Override
    public Player getPlayer(@NonNull UUID uniqueId) {
        return getPlayer(uniqueId, false);
    }

    private Player getPlayer(@NonNull UUID uniqueId, boolean initialize) {
        AtomicReference<Player> atomicPlayer = new AtomicReference<>();
        cloud.getPersistence().runInTransaction(databaseTransaction -> {
            databaseTransaction.suppressCommit();

            if (initialize && cloud.getGameMode() != null) {
                atomicPlayer.set(cloud.getPersistence().executeFiltered("gameMode", Collections.singletonMap("gameMode", cloud.getGameMode()),
                        () -> cloud.getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder()
                                .primaryKeyValue(uniqueId).namedGraph("graph.Player.join").build())
                ));
            } else {
                atomicPlayer.set(cloud.getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder()
                        .primaryKeyValue(uniqueId).build()));
            }
        });

        Player player = atomicPlayer.get();
        if (player != null) {
            ((CloudPlayer)player).setSidedPlayer(sidedPlayerFactory.generateSidedPlayer(uniqueId));
        }

        return player;
    }

    @Override
    public Player getPlayer(@NonNull SidedPlayer sidedPlayer) {
        return getPlayer(sidedPlayer.getUniqueId());
    }

    @Override
    public Player getOrCreatePlayer(@NonNull Object sidedObject) throws PlayerSoftDeletedException {
        return getOrCreatePlayer(sidedPlayerFactory.generateSidedPlayer(sidedObject));
    }

    @Override
    public Player getOrCreatePlayer(@NonNull SidedPlayer sidedObject) throws PlayerSoftDeletedException {
        Player player = getPlayer(sidedObject.getUniqueId(), true);

        if (player != null) {
            return player;
        }

        try {
            return createPlayer(sidedObject.getUniqueId(), sidedObject.getName());
        } catch (EntityExistsException e) {
            throw new PlayerSoftDeletedException(sidedObject.getUniqueId());
        }
    }

    @Override
    public void logoutPlayer(@NonNull Player player) {
        cloud.getPersistence().save(player);
        cloud.getPersistence().removeFromContext(player);
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
        player.setSidedPlayer(sidedPlayerFactory.generateSidedPlayer(uniqueId));

        return player;
    }
}

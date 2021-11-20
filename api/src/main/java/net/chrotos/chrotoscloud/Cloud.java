package net.chrotos.chrotoscloud;

import lombok.Getter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.player.Player;

import java.util.List;

@Getter
public abstract class Cloud {
    private static Cloud instance;
    private PersistenceAdapter persistence;
    private CloudConfig cloudConfig;

    public static void initializeFromInstance(Cloud instance) {
        if (Cloud.instance != null) {
            throw new IllegalStateException("Cloud is already initialized");
        }

        Cloud.instance = instance;
    }

    public abstract List<Player> getOnlinePlayers();
}

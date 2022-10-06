package net.chrotos.chrotoscloud.persistence.mysql;

import com.google.inject.Singleton;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;

import java.util.UUID;

@Singleton
public class MockSidedPlayerFactory implements SidedPlayerFactory {
    @Override
    public SidedPlayer generateSidedPlayer(UUID uuid) {
        return null;
    }

    @Override
    public SidedPlayer generateSidedPlayer(Object sidedObject) {
        return null;
    }
}

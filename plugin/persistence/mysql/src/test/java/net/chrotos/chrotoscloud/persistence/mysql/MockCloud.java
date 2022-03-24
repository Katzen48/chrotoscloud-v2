package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.player.Player;

import java.util.List;

public class MockCloud extends CoreCloud {
    public MockCloud() {
        setCloudConfig(new MockConfig());
    }

    @Override
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }

    @Override
    public GameManager getGameManager() {
        return null;
    }
}

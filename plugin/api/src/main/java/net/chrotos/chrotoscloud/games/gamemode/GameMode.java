package net.chrotos.chrotoscloud.games.gamemode;

import java.util.List;

public interface GameMode {
    String getVersion();
    String getCloudVersion();
    GameModeMaps getMaps();
    List<GameModePlugin> getPlugins();
    GameModeResourcePack getResourcePack();
}

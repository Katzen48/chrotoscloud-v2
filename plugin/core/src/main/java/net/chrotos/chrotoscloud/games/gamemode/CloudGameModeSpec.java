package net.chrotos.chrotoscloud.games.gamemode;

import lombok.Getter;

import java.util.List;

@Getter
public class CloudGameModeSpec {
    private String version;
    private String cloudVersion;
    private GameModeMaps maps;
    private List<GameModePlugin> plugins;
    private GameModeResourcePack resourcePack;
}

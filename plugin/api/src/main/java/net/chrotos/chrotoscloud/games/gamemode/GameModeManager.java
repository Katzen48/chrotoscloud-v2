package net.chrotos.chrotoscloud.games.gamemode;

import lombok.NonNull;

import java.util.List;

public interface GameModeManager {
    List<? extends GameMode> getGameModes();
    GameMode getByName(@NonNull String name);
}

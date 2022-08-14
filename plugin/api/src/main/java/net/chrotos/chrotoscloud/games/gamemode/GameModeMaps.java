package net.chrotos.chrotoscloud.games.gamemode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameModeMaps {
    private boolean random;
    private List<GameModeMap> pool = new ArrayList<>();
}

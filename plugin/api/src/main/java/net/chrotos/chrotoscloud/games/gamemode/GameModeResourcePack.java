package net.chrotos.chrotoscloud.games.gamemode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameModeResourcePack {
    private String url;
    private String hash;
    private boolean required;
}

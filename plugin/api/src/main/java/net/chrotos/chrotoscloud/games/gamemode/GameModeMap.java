package net.chrotos.chrotoscloud.games.gamemode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.chrotos.chrotoscloud.dependencies.MavenLikeDependency;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameModeMap {
    private boolean required;
    private String name;
    private MavenLikeDependency dependency;
}

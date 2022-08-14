package net.chrotos.chrotoscloud.games.gamemode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.chrotos.chrotoscloud.dependencies.MavenDependency;
import net.chrotos.chrotoscloud.dependencies.MavenLikeDependency;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameModePlugin {
    private MavenDependency dependency;
    private MavenLikeDependency configuration;
}

package net.chrotos.chrotoscloud.dependencies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MavenLikeDependency {
    private final MavenLike mavenLike;
    private final String url;
}

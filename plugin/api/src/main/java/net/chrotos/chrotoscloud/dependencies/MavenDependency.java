package net.chrotos.chrotoscloud.dependencies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MavenDependency {
    private final MavenLike maven;
    private final String url;
}

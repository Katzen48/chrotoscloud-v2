package net.chrotos.chrotoscloud.dependencies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MavenLike {
    private final String groupId;
    private final String artifactId;
    private final String version;
}

package net.chrotos.chrotoscloud.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameServerPingRequest {
    @NonNull
    private String name;
}

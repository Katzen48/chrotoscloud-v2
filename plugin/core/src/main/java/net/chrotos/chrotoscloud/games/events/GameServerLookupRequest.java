package net.chrotos.chrotoscloud.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameServerLookupRequest {
    private String gameMode;
}

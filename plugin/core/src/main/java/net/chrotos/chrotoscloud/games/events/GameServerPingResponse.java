package net.chrotos.chrotoscloud.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.chrotos.chrotoscloud.games.CloudGameServer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameServerPingResponse {
    private CloudGameServer gameServer;
}

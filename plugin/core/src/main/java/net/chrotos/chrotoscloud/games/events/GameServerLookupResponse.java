package net.chrotos.chrotoscloud.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.chrotos.chrotoscloud.games.CloudGameServer;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameServerLookupResponse {
    private List<CloudGameServer> gameServers;
}

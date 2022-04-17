package net.chrotos.chrotoscloud.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.chrotos.chrotoscloud.games.CloudGameServer;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameServerConnectedEvent {
    private CloudGameServer from;
    private UUID playerId;

    public boolean hasSwitched() {
        return from != null;
    }
}

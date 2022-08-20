package net.chrotos.chrotoscloud.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PlayerKickedEvent {
    private UUID playerId;
    private String reason;
}

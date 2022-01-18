package net.chrotos.chrotoscloud.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PlayerSoftDeletedException extends RuntimeException {
    private final UUID uniqueId;
}

package net.chrotos.chrotoscloud.velocity.player;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.player.SidedPlayer;

import java.util.Locale;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class VelocitySidedPlayer implements SidedPlayer {
    private final Player sidedObject;

    @Override
    public UUID getUniqueId() {
        return getSidedObject().getUniqueId();
    }

    @Override
    public String getName() {
        return getSidedObject().getUsername();
    }

    @Override
    public Locale getLocale() {
        return getSidedObject().getEffectiveLocale();
    }
}

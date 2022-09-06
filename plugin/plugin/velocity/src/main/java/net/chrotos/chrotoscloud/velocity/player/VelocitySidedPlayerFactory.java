package net.chrotos.chrotoscloud.velocity.player;

import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.chrotos.chrotoscloud.player.SidedPlayerFactory;
import net.chrotos.chrotoscloud.velocity.VelocityCloud;

import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class VelocitySidedPlayerFactory implements SidedPlayerFactory {
    private final VelocityCloud cloud;

    @Override
    public SidedPlayer generateSidedPlayer(UUID uuid) {
        Optional<Player> player = cloud.getProxyServer().getPlayer(uuid);
        if (player.isEmpty()) {
            return null;
        }

        return generateSidedPlayer(player.get());
    }

    @Override
    public SidedPlayer generateSidedPlayer(Object sidedObject) {
        return new VelocitySidedPlayer((Player) sidedObject);
    }
}

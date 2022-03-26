package net.chrotos.chrotoscloud.velocity.player;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.velocity.VelocityCloud;

@RequiredArgsConstructor
public class PermissionsProvider implements PermissionProvider {
    private final VelocityCloud cloud;

    @Override
    public PermissionFunction createFunction(PermissionSubject subject) {
        Player player = (Player) subject;

        final net.chrotos.chrotoscloud.player.Player cloudPlayer = cloud.getPlayerManager().getPlayer(player.getUniqueId());

        return permission -> Tristate.fromBoolean(
                cloudPlayer.hasPermission(permission)
        );
    }
}

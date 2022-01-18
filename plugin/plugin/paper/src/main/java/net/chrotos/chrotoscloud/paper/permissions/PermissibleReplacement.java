package net.chrotos.chrotoscloud.paper.permissions;

import lombok.NonNull;
import net.chrotos.chrotoscloud.paper.PaperCloud;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class PermissibleReplacement extends PermissibleBase {
    private final Player player;
    private final PaperCloud cloud;

    public PermissibleReplacement(@Nullable ServerOperator opable, @NonNull Player player, @NonNull PaperCloud cloud) {
        super(opable);

        this.player = player;
        this.cloud = cloud;
    }

    @Override
    public boolean hasPermission(@NotNull String inName) {
        return cloud.getPlayerManager().getPlayer(player.getUniqueId()).hasPermission(inName);
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return true;
    }

    @Override
    public synchronized @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Collections.emptySet();
    }

    @Override
    public boolean isOp() {
        return hasPermission("minecraft.command.op");
    }

    @Override
    public synchronized void recalculatePermissions() {
        if (cloud != null) {
            cloud.getPlayerManager().getPlayer(player.getUniqueId()).clearPermissionsCache();
        }
    }
}

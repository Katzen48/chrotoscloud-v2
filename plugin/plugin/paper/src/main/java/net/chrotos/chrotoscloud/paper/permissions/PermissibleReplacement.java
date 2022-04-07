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
    private final net.chrotos.chrotoscloud.player.Player cloudPlayer;

    public PermissibleReplacement(@Nullable ServerOperator opable, @NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer) {
        super(opable);

        this.cloudPlayer = cloudPlayer;
    }

    @Override
    public boolean hasPermission(@NotNull String inName) {
        return cloudPlayer.hasPermission(inName);
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
        if (cloudPlayer != null) {
            cloudPlayer.clearPermissionsCache();
        }
    }
}

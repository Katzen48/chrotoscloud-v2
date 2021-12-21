package net.chrotos.chrotoscloud.permissions;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface Permissible {
    boolean hasPermission(@NonNull String permission);
    boolean hasPermission(@NonNull String permission, boolean ignoreCache);
    List<Permission> getPermissions();
    UUID getUniqueId();
    String getName();
}

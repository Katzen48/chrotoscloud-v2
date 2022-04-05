package net.chrotos.chrotoscloud.permissions;

import lombok.NonNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Permissible {
    boolean hasPermission(@NonNull String permission);
    boolean hasPermission(@NonNull String permission, boolean ignoreCache);
    void clearPermissionsCache();
    @NonNull
    Set<Permission> getPermissions();
    @NonNull
    UUID getUniqueId();
    @NonNull
    String getName();
    @NonNull
    Optional<Permission> getPermissionExact(@NonNull String permission);
}

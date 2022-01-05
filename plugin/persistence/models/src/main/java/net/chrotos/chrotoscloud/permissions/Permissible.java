package net.chrotos.chrotoscloud.permissions;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Permissible {
    boolean hasPermission(@NonNull String permission);
    boolean hasPermission(@NonNull String permission, boolean ignoreCache);
    void clearCache();
    @NonNull
    List<Permission> getPermissions();
    @NonNull
    UUID getUniqueId();
    @NonNull
    String getName();
    @NonNull
    Optional<Permission> getPermissionExact(@NonNull String permission);
}

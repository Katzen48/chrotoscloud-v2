package net.chrotos.chrotoscloud.permissions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Optional;

@MappedSuperclass
public abstract class CloudPermissible implements Permissible {
    private final transient LoadingCache<String, Boolean> permissionsCache = CacheBuilder.newBuilder()
                                                        .expireAfterWrite(Duration.ofSeconds(60))
                                                        .build(CacheLoader.from(this::calculatePermission));

    @Override
    public boolean hasPermission(@NonNull String permission) {
        return hasPermission(permission, false);
    }

    @Override
    public boolean hasPermission(@NonNull String permission, boolean ignoreCache) {
        try {
            if (ignoreCache) {
                permissionsCache.invalidate(permission);
            }

            return permissionsCache.get(permission.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void clearPermissionsCache() {
        permissionsCache.invalidateAll();
    }

    public boolean calculatePermission(@NonNull String permission) {
        Optional<Permission> optional = getPermissionExact(permission);

        if (optional.isPresent()) {
            return optional.get().getValue();
        }

        String permPart = permission;
        int index;
        while ((index = permPart.lastIndexOf('.')) >= 0) {
            permPart = permPart.substring(0, index);

            optional = getPermissionExact(permPart + ".*");

            if (optional.isPresent()) {
                return optional.get().getValue();
            }
        }

        return false;
    }

    @Transactional
    @Override
    @NonNull
    public Optional<Permission> getPermissionExact(@NonNull String permission) {
        return getPermissions().stream().filter(permissionFilter ->
                permissionFilter.getName().equalsIgnoreCase(permission)
        ).findFirst();
    }
}

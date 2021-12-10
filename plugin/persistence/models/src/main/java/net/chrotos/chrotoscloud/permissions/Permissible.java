package net.chrotos.chrotoscloud.permissions;

import java.util.Collection;

public interface Permissible {
    boolean hasPermission(String permission);
    boolean hasPermission(Permission permission);
    Collection<Permission> getPermissions();
}

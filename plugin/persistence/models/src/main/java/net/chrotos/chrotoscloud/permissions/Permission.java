package net.chrotos.chrotoscloud.permissions;

import net.chrotos.chrotoscloud.Model;

public interface Permission extends Model {
    String getName();
    boolean getValue();
}

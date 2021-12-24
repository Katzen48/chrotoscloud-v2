package net.chrotos.chrotoscloud.velocity;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;

public class PermissionsProvider implements PermissionProvider {
    @Override
    public PermissionFunction createFunction(PermissionSubject subject) {
        return new PermissionFunction() {
            @Override
            public Tristate getPermissionValue(String permission) {
                return Tristate.TRUE; // TODO
            }
        };
    }
}

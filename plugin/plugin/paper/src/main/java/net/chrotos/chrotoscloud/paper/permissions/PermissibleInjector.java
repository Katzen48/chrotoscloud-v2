package net.chrotos.chrotoscloud.paper.permissions;

import lombok.NonNull;
import net.chrotos.chrotoscloud.paper.PaperCloud;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

import java.lang.reflect.Field;

public class PermissibleInjector {
    private static final Field HUMAN_ENTITY_PERMISSIBLE_FIELD;
    private static final Field PERMISSIBLE_BASE_OPABLE_FIELD;

    public static void inject(@NonNull Player player, @NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer) throws ReflectiveOperationException {
        PermissibleBase oldPermissible = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);
        ServerOperator opable = (ServerOperator) PERMISSIBLE_BASE_OPABLE_FIELD.get(oldPermissible);
        HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, new PermissibleReplacement(opable, cloudPlayer));
    }

    static {
        String craftBukkitPackage = Bukkit.getServer().getClass().getPackage().getName() + ".";

        try {
            HUMAN_ENTITY_PERMISSIBLE_FIELD = Class.forName(craftBukkitPackage + "entity.CraftHumanEntity")
                                                    .getDeclaredField("perm");
            HUMAN_ENTITY_PERMISSIBLE_FIELD.setAccessible(true);

            PERMISSIBLE_BASE_OPABLE_FIELD = PermissibleBase.class.getDeclaredField("opable");
            PERMISSIBLE_BASE_OPABLE_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

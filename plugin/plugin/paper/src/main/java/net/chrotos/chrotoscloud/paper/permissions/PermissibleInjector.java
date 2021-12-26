package net.chrotos.chrotoscloud.paper.permissions;

import net.chrotos.chrotoscloud.paper.PaperCloud;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissibleInjector {
    private static final Field HUMAN_ENTITY_PERMISSIBLE_FIELD;
    private static final Field PERMISSIBLE_BASE_OPABLE_FIELD;

    public static void inject(Player player, PaperCloud cloud) throws ReflectiveOperationException {
        PermissibleBase oldPermissible = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);
        ServerOperator opable = (ServerOperator) PERMISSIBLE_BASE_OPABLE_FIELD.get(oldPermissible);
        HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, new PermissibleReplacement(opable, player, cloud));
    }

    static {
        Class<?> craftBukkitServer = Bukkit.getServer().getClass();
        Matcher matcher = Pattern.compile("^org\\\\.bukkit\\\\.craftbukkit\\\\.(\\\\w+)\\\\.CraftServer$")
                                    .matcher(craftBukkitServer.getName());

        String craftBukkitPackage;
        if (matcher.matches()) {
            craftBukkitPackage = "org.bukkit.craftbukkit." + matcher.group(1) + ".";
        } else {
            throw new IllegalStateException("Unsupported Bukkit implementation");
        }

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

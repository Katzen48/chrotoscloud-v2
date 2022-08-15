package net.chrotos.chrotoscloud.mixin.core;

import net.chrotos.chrotoscloud.Cloud;
import org.bukkit.event.Event;
import org.bukkit.plugin.SimplePluginManager;
import org.spongepowered.asm.mixin.*;

@Mixin(SimplePluginManager.class)
public abstract class MixinPluginManager {
    @Shadow(prefix = "super$")
    public abstract void super$fireEvent(Event event);

    @Overwrite
    private void fireEvent(Event event) {
        Cloud.getInstance().getPersistence().runInTransaction(databaseTransaction -> {
            super$fireEvent(event);
        });
    }
}

package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.Cloud;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.lang.reflect.Field;
import java.util.Map;

@Plugin(name = "ChrotosCloud", version = "3.0-SNAPSHOT")
@Author("Katzen48")
@LoadOrder(PluginLoadOrder.STARTUP)
@ApiVersion(ApiVersion.Target.v1_18)
public class CloudPlugin extends JavaPlugin {
    private final PaperCloud cloud;
    private PaperEventHandler eventHandler;

    public CloudPlugin() {
        Cloud.setServiceClassLoader(getClassLoader());
        this.cloud = (PaperCloud) Cloud.getInstance();
        this.cloud.setPlugin(this);
    }

    @Override
    public void onLoad() {
        cloud.load();
        this.cloud.setLogger(getLogger());
    }

    @Override
    public void onEnable() {
        cloud.initialize();

        eventHandler = new PaperEventHandler(cloud);
        getServer().getPluginManager().registerEvents(eventHandler, this);

        tryUnregisterReloadCommands();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(eventHandler::onPlayerLeave);
    }

    private void tryUnregisterReloadCommands() {
        try {
            Field knownCommandsField = org.bukkit.command.SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(getServer().getCommandMap());
            knownCommands.remove("reload");
            knownCommands.remove("bukkit:reload");
            knownCommands.remove("bukkit:rl");
            knownCommands.remove("spigot:reload");
            knownCommands.remove("paper:paper");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

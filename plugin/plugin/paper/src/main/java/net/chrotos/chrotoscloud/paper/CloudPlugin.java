package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.Cloud;
import org.bukkit.command.Command;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

@Plugin(name = "ChrotosCloud", version = "3.0-SNAPSHOT")
@Author("Katzen48")
@LoadOrder(PluginLoadOrder.STARTUP)
@ApiVersion(ApiVersion.Target.v1_18)
public class CloudPlugin extends JavaPlugin {
    private final PaperCloud cloud;

    public CloudPlugin() {
        Cloud.setServiceClassLoader(getClassLoader());
        this.cloud = (PaperCloud) Cloud.getInstance();
    }

    @Override
    public void onLoad() {
        cloud.load();
    }

    @Override
    public void onEnable() {
        cloud.initialize();

        // Load properties
        byte opLevel = 4;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("server.properties"));

            opLevel = Byte.parseByte(properties.getProperty("op-permission-level", "4"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new PaperEventHandler(opLevel, cloud), this);

        tryUnregisterReloadCommands();
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

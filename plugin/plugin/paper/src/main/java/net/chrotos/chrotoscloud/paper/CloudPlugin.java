package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.Cloud;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "ChrotosCloud", version = "3.0-SNAPSHOT")
@Author("Katzen48")
@LoadOrder(PluginLoadOrder.STARTUP)
public class CloudPlugin extends JavaPlugin {
    private final PaperCloud cloud;

    public CloudPlugin() {
        this.cloud = (PaperCloud) Cloud.getInstance();
    }

    @Override
    public void onLoad() {
        cloud.load();
    }

    @Override
    public void onEnable() {
        cloud.initialize();
    }
}

package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.CoreCloud;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudPlugin extends JavaPlugin {
    private final CoreCloud cloud;

    public CloudPlugin() {
        this.cloud = new PaperCloud(this);
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

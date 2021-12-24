package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CoreCloud;
import org.bukkit.plugin.java.JavaPlugin;

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

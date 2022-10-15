package net.chrotos.chrotoscloud.paper;

import lombok.Getter;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Plugin(name = "ChrotosCloud", version = "3.0-SNAPSHOT")
@Author("Katzen48")
@LoadOrder(PluginLoadOrder.STARTUP)
@ApiVersion(ApiVersion.Target.v1_18)
public class CloudPlugin extends JavaPlugin {
    private final PaperCloud cloud;
    private PaperEventHandler eventHandler;
    @Getter
    private final ExecutorService executorService = Executors.newWorkStealingPool(5);

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

        tryUnregisterCommands();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> eventHandler.onPlayerLeave(player, true));

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        cloud.getScheduler().close();
    }

    private void tryUnregisterCommands() {
        try {
            Field knownCommandsField = org.bukkit.command.SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(getServer().getCommandMap());
            // Reload Commands
            knownCommands.remove("reload");
            knownCommands.remove("bukkit:reload");
            knownCommands.remove("bukkit:rl");
            knownCommands.remove("spigot:reload");
            knownCommands.remove("paper");
            knownCommands.remove("paper:paper");

            // Ban Commands
            knownCommands.remove("ban");
            knownCommands.remove("minecraft:ban");
            knownCommands.remove("essentials:ban");
            knownCommands.remove("banip");
            knownCommands.remove("minecraft:ban-ip");
            knownCommands.remove("essentials:banip");
            // Unban Commands
            knownCommands.remove("unban");
            knownCommands.remove("pardon");
            knownCommands.remove("minecraft:pardon");
            knownCommands.remove("essentials:unban");
            knownCommands.remove("essentials:pardon");
            knownCommands.remove("unbanip");
            knownCommands.remove("pardonip");
            knownCommands.remove("pardon-ip");
            knownCommands.remove("minecraft:pardon-ip");
            knownCommands.remove("essentials:pardonip");
            knownCommands.remove("essentials:unbanip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

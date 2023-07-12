package net.chrotos.chrotoscloud.paper;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.paper.games.PaperGameManager;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Getter
public class PaperCloud extends CoreCloud {
    @Setter
    private Logger logger;
    private final boolean inventorySavingEnabled;
    @Setter
    private CloudPlugin plugin;
    private final PaperScheduler scheduler;

    public PaperCloud() throws IOException {
        this.scheduler = getServiceInjector().getInstance(PaperScheduler.class);
        setCloudConfig(new PaperConfig());
        // TODO move to game manager
        inventorySavingEnabled = Bukkit.getServer().spigot().getSpigotConfig().getBoolean("players.disable-saving");
    }

    @Override
    public void initialize() {
        super.initialize();

        scheduler.initialize();
    }

    @Override
    @NonNull
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }

    @Override
    public File getTranslationDir() {
        return new File(plugin.getDataFolder(), "translations");
    }
}

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
    private final PaperGameManager gameManager;
    private final boolean inventorySavingEnabled;
    @Setter
    private CloudPlugin plugin;
    @NonNull
    private Injector serviceInjector;

    public PaperCloud() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getServiceClassLoader());
        this.serviceInjector = Guice.createInjector(new PaperModule(this));
        Thread.currentThread().setContextClassLoader(loader);

        setCloudConfig(new PaperConfig());
        gameManager = getServiceInjector().getInstance(PaperGameManager.class);
        inventorySavingEnabled = Bukkit.getServer().spigot().getSpigotConfig().getBoolean("players.disable-saving");
    }

    @Override
    public void initialize() {
        super.initialize();

        try {
            gameManager.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

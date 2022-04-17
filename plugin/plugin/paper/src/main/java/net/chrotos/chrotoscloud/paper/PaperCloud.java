package net.chrotos.chrotoscloud.paper;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.paper.games.PaperGameManager;
import org.bukkit.Bukkit;

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

    public PaperCloud() throws IOException {
        setCloudConfig(new PaperConfig());
        gameManager = new PaperGameManager(this);
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
}

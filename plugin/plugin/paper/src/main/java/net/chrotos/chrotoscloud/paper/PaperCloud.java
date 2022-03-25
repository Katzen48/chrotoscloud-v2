package net.chrotos.chrotoscloud.paper;

import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.games.GameManager;
import net.chrotos.chrotoscloud.paper.games.PaperGameManager;
import org.bukkit.Bukkit;

import java.io.IOException;

@Getter
public class PaperCloud extends CoreCloud {
    private final GameManager gameManager;
    private final boolean inventorySavingEnabled;

    public PaperCloud() throws IOException {
        setCloudConfig(new PaperConfig());
        gameManager = new PaperGameManager(this);
        inventorySavingEnabled = Bukkit.getServer().spigot().getSpigotConfig().getBoolean("players.disable-saving");
    }

    @Override
    @NonNull
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }
}

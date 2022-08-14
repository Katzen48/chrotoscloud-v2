package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.games.gamemode.GameMode;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PaperConfig implements CloudConfig {
    private final Properties podLabels;
    private GameMode gameModeSpec;

    protected PaperConfig() throws IOException {
        this.podLabels = new Properties();
        this.podLabels.load(new FileReader("/etc/podinfo/labels"));
    }

    @Override
    public String getPersistenceConnectionString() {
        return System.getenv("DB_STRING");
    }

    @Override
    public String getPersistenceUser() {
        return System.getenv("DB_USER");
    }

    @Override
    public String getPersistencePassword() {
        return System.getenv("DB_PASSWORD");
    }

    @Override
    public String getCacheHost() {
        return System.getenv("CACHE_HOST");
    }

    @Override
    public int getCachePort() {
        return Integer.parseInt(System.getenv("CACHE_PORT"));
    }

    @Override
    public String getCachePassword() {
        return System.getenv("CACHE_PASSWORD");
    }

    @Override
    public String getQueueHost() {
        return System.getenv("QUEUE_HOST");
    }

    @Override
    public int getQueuePort() {
        return Integer.parseInt(System.getenv("QUEUE_PORT"));
    }

    @Override
    public String getQueueUser() {
        return System.getenv("QUEUE_USER");
    }

    @Override
    public String getQueuePassword() {
        return System.getenv("QUEUE_PASSWORD");
    }

    @Override
    public String getGameMode() {
        return podLabels.getProperty("net.chrotos.chrotoscloud.gameserver/gamemode").replaceAll("\"", "");
    }

    @Override
    public String getResourcePackUrl() {
        return getGameModeSpec().getResourcePack() != null ? getGameModeSpec().getResourcePack().getUrl() : null;
    }

    @Override
    public String getResourcePackHash() {
        return getGameModeSpec().getResourcePack() != null ? getGameModeSpec().getResourcePack().getHash() : null;
    }

    @Override
    public boolean getResourcePackRequired() {
        return getGameModeSpec().getResourcePack() != null && getGameModeSpec().getResourcePack().isRequired();
    }

    @Override
    public TextComponent getResourcePackPrompt() {
        return LegacyComponentSerializer.builder().build().deserialize(Bukkit.getResourcePackPrompt());
    }

    @Override
    public boolean shouldRunMigrations() {
        String value = System.getenv("DB_UPGRADE");

        return value != null && (value.equalsIgnoreCase("1") ||value.equalsIgnoreCase("true"));
    }

    private GameMode getGameModeSpec() {
        if (gameModeSpec != null) {
            return gameModeSpec;
        }

        return (gameModeSpec = Cloud.getInstance().getGameManager().getGameModeManager().getByName(getGameMode()));
    }
}

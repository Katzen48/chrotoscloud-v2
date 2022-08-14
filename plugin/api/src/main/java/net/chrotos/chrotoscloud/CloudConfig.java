package net.chrotos.chrotoscloud;

import net.kyori.adventure.text.TextComponent;

public interface CloudConfig {
    String getPersistenceConnectionString();
    String getPersistenceUser();
    String getPersistencePassword();

    String getCacheHost();
    int getCachePort();
    String getCachePassword();

    String getQueueHost();
    int getQueuePort();
    String getQueueUser();
    String getQueuePassword();

    String getGameMode();
    String getResourcePackUrl();
    String getResourcePackHash();
    boolean getResourcePackRequired();
    TextComponent getResourcePackPrompt();

    boolean shouldRunMigrations();
}

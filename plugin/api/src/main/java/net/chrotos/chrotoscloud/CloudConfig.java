package net.chrotos.chrotoscloud;

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

    boolean shouldRunMigrations();
}

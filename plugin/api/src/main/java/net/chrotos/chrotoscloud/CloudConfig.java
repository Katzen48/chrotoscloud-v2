package net.chrotos.chrotoscloud;

public interface CloudConfig {
    String getPersistenceConnectionString();
    String getPersistenceUser();
    String getPersistencePassword();
    String getCacheHost();
    int getCachePort();
    String getCachePassword();
    boolean shouldRunMigrations();
}

package net.chrotos.chrotoscloud;

public interface CloudConfig {
    String getPersistenceConnectionString();
    String getPersistenceUser();
    String getPersistencePassword();
    boolean shouldRunMigrations();
}

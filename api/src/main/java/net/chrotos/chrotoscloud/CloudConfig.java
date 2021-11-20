package net.chrotos.chrotoscloud;

public interface CloudConfig {
    String getPersistenceAdapter();
    String getPersistenceConnectionString();
    String getPersistenceUser();
    String getPersistencePassword();
}

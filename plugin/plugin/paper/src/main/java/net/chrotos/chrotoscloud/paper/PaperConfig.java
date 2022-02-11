package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.CloudConfig;

public class PaperConfig implements CloudConfig {
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
    public boolean shouldRunMigrations() {
        String value = System.getenv("DB_UPGRADE");

        return value != null && (value.equalsIgnoreCase("1") ||value.equalsIgnoreCase("true"));
    }
}

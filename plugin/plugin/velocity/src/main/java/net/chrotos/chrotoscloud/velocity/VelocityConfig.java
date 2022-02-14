package net.chrotos.chrotoscloud.velocity;

import net.chrotos.chrotoscloud.CloudConfig;

public class VelocityConfig implements CloudConfig {
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
    public boolean shouldRunMigrations() {
        String value = System.getenv("DB_UPGRADE");

        return value != null && (value.equalsIgnoreCase("1") ||value.equalsIgnoreCase("true"));
    }
}

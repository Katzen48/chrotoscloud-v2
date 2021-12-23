package net.chrotos.chrotoscloud.paper;

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
    public boolean shouldRunMigrations() {
        String value = System.getenv("DB_UPGRADE");

        return value != null && (value.equalsIgnoreCase("1") ||value.equalsIgnoreCase("true"));
    }
}
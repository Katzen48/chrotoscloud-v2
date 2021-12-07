package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.CloudConfig;

public class MockConfig implements CloudConfig {
    @Override
    public String getPersistenceConnectionString() {
        return System.getenv("CHROTOSCLOUD_TEST_DB_STRING");
    }

    @Override
    public String getPersistenceUser() {
        return System.getenv("CHROTOSCLOUD_TEST_DB_USER");
    }

    @Override
    public String getPersistencePassword() {
        return System.getenv("CHROTOSCLOUD_TEST_DB_PASSWORD");
    }

    @Override
    public boolean shouldRunMigrations() {
        return true;
    }
}

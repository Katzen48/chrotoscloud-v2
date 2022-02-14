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
    public String getCacheHost() {
        return System.getenv("CHROTOSCLOUD_TEST_CACHE_HOST");
    }

    @Override
    public int getCachePort() {
        return Integer.parseInt(System.getenv("CHROTOSCLOUD_TEST_CACHE_PORT"));
    }

    @Override
    public String getCachePassword() {
        return System.getenv("CHROTOSCLOUD_TEST_CACHE_PASSWORD");
    }

    @Override
    public String getQueueHost() {
        return System.getenv("CHROTOSCLOUD_TEST_QUEUE_HOST");
    }

    @Override
    public int getQueuePort() {
        return Integer.parseInt(System.getenv("CHROTOSCLOUD_TEST_QUEUE_PORT"));
    }

    @Override
    public String getQueueUser() {
        return System.getenv("CHROTOSCLOUD_TEST_QUEUE_USER");
    }

    @Override
    public String getQueuePassword() {
        return System.getenv("CHROTOSCLOUD_TEST_QUEUE_PASSWORD");
    }

    @Override
    public boolean shouldRunMigrations() {
        return true;
    }
}

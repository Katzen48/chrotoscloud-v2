package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.games.stats.CloudGameStatistic;
import net.chrotos.chrotoscloud.games.stats.GameStatistic;
import net.chrotos.chrotoscloud.permissions.CloudPermission;
import net.chrotos.chrotoscloud.permissions.CloudRank;
import net.chrotos.chrotoscloud.permissions.Permission;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MysqlPersistenceTest {
    @Test
    @Order(0)
    public void testConnectivity() {
        Cloud cloud = Cloud.getInstance();

        cloud.load();
        assertTrue(cloud.isLoaded());
        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence());
        assertTrue(cloud.getPersistence() instanceof MysqlPersistenceAdapter);
    }

    @Test
    @Order(1)
    public void testPlayerPersistence() {
        Cloud cloud = Cloud.getInstance();

        cloud.load();
        assertTrue(cloud.isLoaded());
        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence().getAll(CloudPlayer.class));

        Player player = new CloudPlayer(UUID.randomUUID(), "test");
        cloud.getPersistence().save(player);
        assertFalse(cloud.getPersistence().getAll(CloudPlayer.class).isEmpty());
        assertNotNull(cloud.getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder().primaryKeyValue(player.getUniqueId()).build()));
    }

    @Test
    @Order(2)
    public void testAccountPersistence() {
        Cloud cloud = Cloud.getInstance();

        cloud.load();
        assertTrue(cloud.isLoaded());
        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence().getAll(CloudAccount.class));

        Player player = new CloudPlayer(UUID.randomUUID(), "test");
        cloud.getPersistence().save(player);

        cloud.getPersistence().runInTransaction((databaseTransaction) -> {
            CloudAccount account = new CloudAccount(player, AccountType.BANK);
            cloud.getPersistence().save(account);

            assertFalse(player.getAccounts().isEmpty());
            assertFalse(cloud.getPersistence().getAll(CloudAccount.class).isEmpty());
        });
    }

    @Test
    @Order(2)
    public void testPermissionPersistenceAndCheck() {
        Cloud cloud = Cloud.getInstance();

        cloud.load();
        assertTrue(cloud.isLoaded());
        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence().getAll(CloudPermission.class));

        Player player = new CloudPlayer(UUID.randomUUID(), "test");
        cloud.getPersistence().save(player);

        cloud.getPersistence().runInTransaction((databaseTransaction) -> {
            CloudPermission permission = new CloudPermission(UUID.randomUUID(), "test.test", false, player);
            player.getPermissions().add(permission);

            List<? extends Permission> permissions = cloud.getPersistence().getAll(CloudPermission.class);

            assertFalse(permissions.isEmpty());
            assertTrue(permissions.get(permissions.size() - 1) instanceof CloudPermission);
            assertTrue(((CloudPermission) permissions.get(permissions.size() - 1)).getPermissible() instanceof CloudPlayer);

            assertFalse(player.hasPermission("test.test", true));
            assertFalse(player.hasPermission("test.*", true));
            assertFalse(player.hasPermission("test.test2", true));

            CloudPermission permission2 = new CloudPermission(UUID.randomUUID(), "test.*", true, player);
            player.getPermissions().add(permission2);

            assertFalse(player.hasPermission("test.test", true));
            assertTrue(player.hasPermission("test.*", true));
            assertTrue(player.hasPermission("test.test2", true));
            assertFalse(player.hasPermission("test2.test"));

            CloudRank rank = new CloudRank(UUID.randomUUID(), UUID.randomUUID().toString(), "test");
            cloud.getPersistence().save(rank);
            player.setRank(rank);
            rank.getPermissions().add(new CloudPermission(UUID.randomUUID(), "test3.*", true, rank));
            CloudRank parentRank = new CloudRank(UUID.randomUUID(), UUID.randomUUID().toString(), "test2");
            cloud.getPersistence().save(parentRank);
            rank.setParent(parentRank);
            parentRank.getPermissions().add(new CloudPermission(UUID.randomUUID(), "test4.*", true, parentRank));

            assertTrue(player.hasPermission("test3.test"));
            assertTrue(player.hasPermission("test4.test"));
        });
    }

    @Test
    @Order(3)
    public void testGameStats() {
        Cloud cloud = Cloud.getInstance();

        cloud.load();
        assertTrue(cloud.isLoaded());
        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence().getAll(CloudGameStatistic.class));

        Player player = new CloudPlayer(UUID.randomUUID(), "test");
        cloud.getPersistence().save(player);

        cloud.getPersistence().runInTransaction(databaseTransaction -> {
            CloudGameStatistic gameStatistic = new CloudGameStatistic(UUID.randomUUID(), "test", "lobby-test", player, 1);
            player.getStats().add(gameStatistic);

            List<? extends GameStatistic> statistics = cloud.getPersistence().getAll(CloudGameStatistic.class);

            assertFalse(statistics.isEmpty());
            assertTrue(statistics.get(statistics.size() - 1) instanceof CloudGameStatistic);
            assertTrue(statistics.get(statistics.size() - 1).getPlayer() instanceof CloudPlayer);

            assertEquals(statistics.get(statistics.size() - 1).getValue(), 1);

            GameStatistic statistic = statistics.get(statistics.size() - 1);
            statistic.setValue(2);
            cloud.getPersistence().save(statistic);

            statistics = cloud.getPersistence().getAll(CloudGameStatistic.class);
            assertEquals(statistics.get(statistics.size() - 1).getValue(), 2);
        });
    }
}

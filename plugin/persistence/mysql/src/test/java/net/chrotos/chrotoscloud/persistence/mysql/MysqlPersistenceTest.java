package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.permissions.CloudPermission;
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

        assertNotNull(cloud.getPersistence());
        assertTrue(cloud.getPersistence() instanceof MysqlPersistenceAdapter);
    }

    @Test
    @Order(1)
    public void testPlayerPersistence() {
        Cloud cloud = Cloud.getInstance();

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

        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence().getAll(CloudAccount.class));

        Player player = new CloudPlayer(UUID.randomUUID(), "test");
        cloud.getPersistence().save(player);

        cloud.getPersistence().runInTransaction(() -> {
            Account account = new CloudAccount(player, AccountType.BANK);
            Player finalPlayer = player;
            finalPlayer.getAccounts().add(account);

            assertFalse(player.getAccounts().isEmpty());
            assertFalse(cloud.getPersistence().getAll(CloudAccount.class).isEmpty());
        });
    }

    @Test
    @Order(2)
    public void testPermissionPersistenceAndCheck() {
        Cloud cloud = Cloud.getInstance();

        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence().getAll(CloudPermission.class));

        Player player = new CloudPlayer(UUID.randomUUID(), "test");
        cloud.getPersistence().save(player);

        cloud.getPersistence().runInTransaction(() -> {
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
        });
    }
}

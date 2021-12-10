package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.junit.jupiter.api.*;

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
    @Order(1)
    public void testAccountPersistence() {
        Cloud cloud = Cloud.getInstance();

        cloud.initialize();
        assertTrue(cloud.isInitialized());

        assertNotNull(cloud.getPersistence().getAll(CloudAccount.class));

        CloudPlayer player = new CloudPlayer(UUID.randomUUID(), "test");
        cloud.getPersistence().save(player);

        Account account = new CloudAccount(player, AccountType.BANK);
        cloud.getPersistence().save(account);
        assertFalse(cloud.getPersistence().getAll(CloudAccount.class).isEmpty());
    }
}

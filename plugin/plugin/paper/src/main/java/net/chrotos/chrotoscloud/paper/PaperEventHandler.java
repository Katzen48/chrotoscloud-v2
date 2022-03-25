package net.chrotos.chrotoscloud.paper;

import com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.paper.chat.PaperChatRenderer;
import net.chrotos.chrotoscloud.paper.permissions.PermissibleInjector;
import net.chrotos.chrotoscloud.player.CloudPlayerInventory;
import net.chrotos.chrotoscloud.player.PlayerInventory;
import net.chrotos.chrotoscloud.player.PlayerSoftDeletedException;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@RequiredArgsConstructor
public class PaperEventHandler implements Listener {
    private final byte opLevel;
    private final PaperCloud cloud;
    private final PaperChatRenderer renderer = new PaperChatRenderer();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        try {
            net.chrotos.chrotoscloud.player.Player cloudPlayer = cloud.getPlayerManager().getOrCreatePlayer(new PaperSidedPlayer(player));
            PermissibleInjector.inject(player, cloud);

            player.sendOpLevel(player.hasPermission("minecraft.command.op") ? opLevel : (byte) 0);

            if (cloud.isInventorySavingEnabled()) {
                loadInventory(cloudPlayer, player);
            }
        } catch (PlayerSoftDeletedException e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("Your account has been deleted!")); // TODO: Translate
        } catch (Exception e) {
            e.printStackTrace();
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("An error occured!")); // TODO: Translate
            cloud.getPlayerManager().logoutPlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProfileWhitelistVerify(ProfileWhitelistVerifyEvent event) {
        if (event.getPlayerProfile().getId() == null) {
            return;
        }

        try {
            net.chrotos.chrotoscloud.player.Player player = cloud.getPlayerManager().getOrCreatePlayer(event.getPlayerProfile().getId(),
                                                       event.getPlayerProfile().getName());

            event.setWhitelisted(event.isWhitelisted() || event.isOp() || player.hasPermission("minecraft.command.op")); // TODO: remove op?
        } catch (PlayerSoftDeletedException e) {
            event.setWhitelisted(false);
            event.kickMessage(Component.text("Your account has been deleted!")); // TODO: Translate
        } catch (Exception e) {
            e.printStackTrace();
            event.setWhitelisted(false);
            event.kickMessage(Component.text("An error occured!")); // TODO: Translate
            cloud.getPlayerManager().logoutPlayer(event.getPlayerProfile().getId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            net.chrotos.chrotoscloud.player.Player cloudPlayer = cloud.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
            if (cloudPlayer == null) {
                return;
            }

            if (cloud.isInventorySavingEnabled()) {
                saveInventory(cloudPlayer, event.getPlayer());
            }

            cloud.getPlayerManager().logoutPlayer(cloudPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent event) {
        event.renderer(renderer);
    }

    private void loadInventory(@NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer, Player player) throws InvalidConfigurationException {
        PlayerInventory inventory = cloudPlayer.getInventory(cloud.getGameMode());
        if (inventory != null) {
            YamlConfiguration inventoryContent = new YamlConfiguration();
            inventoryContent.loadFromString(inventory.getContent());

            ItemStack[] contents = new ItemStack[player.getInventory().getSize()];
            for (int i = 0; i < contents.length; i++) {
                String key = String.valueOf(i);
                if (inventoryContent.contains(key)) {
                    contents[i] = inventoryContent.getItemStack(key);
                }
            }

            player.getInventory().setContents(contents);
        }
    }

    private void saveInventory(@NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer, @NonNull Player player) {
        cloud.getPersistence().runInTransaction(databaseTransaction -> {
            PlayerInventory inventory = cloudPlayer.getInventory(cloud.getGameMode());

            YamlConfiguration inventoryContent = new YamlConfiguration();
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                inventoryContent.set(String.valueOf(i), player.getInventory().getItem(i));
            }

            if (inventory == null) {
                inventory = new CloudPlayerInventory(UUID.randomUUID(), cloud.getGameMode(), cloudPlayer, inventoryContent.saveToString());
                cloud.getPersistence().save(inventory);
            } else {
                inventory.setContent(inventoryContent.saveToString());
                cloud.getPersistence().merge(inventory);
            }
        });
    }
}

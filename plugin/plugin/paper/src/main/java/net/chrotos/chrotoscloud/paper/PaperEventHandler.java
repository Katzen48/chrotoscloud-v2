package net.chrotos.chrotoscloud.paper;

import com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.games.states.CloudGameState;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.paper.chat.PaperChatRenderer;
import net.chrotos.chrotoscloud.paper.permissions.PermissibleInjector;
import net.chrotos.chrotoscloud.player.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class PaperEventHandler implements Listener {
    private final Gson gson = new Gson();
    private final PaperCloud cloud;
    private final PaperChatRenderer renderer = new PaperChatRenderer();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        cloud.getPersistence().runInTransaction(databaseTransaction -> {
            try {
                databaseTransaction.suppressCommit();

                net.chrotos.chrotoscloud.player.Player cloudPlayer = cloud.getPlayerManager().getOrCreatePlayer(new PaperSidedPlayer(player));

                Ban ban = getBan(cloudPlayer);
                if (ban != null) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ban.getBanMessage(player.locale()));

                    return;
                }

                PermissibleInjector.inject(player, cloudPlayer);
                player.setOp(player.hasPermission("minecraft.command.op"));

                if (cloud.isInventorySavingEnabled()) {
                    loadInventory(cloudPlayer, player);
                }

                loadScoreboardTags(cloudPlayer, player);
            } catch (PlayerSoftDeletedException e) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.translatable("cloud.player.deleted", NamedTextColor.RED));
            } catch (Exception e) {
                e.printStackTrace();
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.translatable("cloud.error", NamedTextColor.RED));
                cloud.getPlayerManager().logoutPlayer(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Cloud.getInstance().getPersistence().runInTransaction(transaction -> {
            transaction.suppressCommit();

            net.chrotos.chrotoscloud.player.Player cloudPlayer = cloud.getPlayerManager().getOrCreatePlayer(new PaperSidedPlayer(event.getPlayer()));

            Ban ban = getBan(cloudPlayer);
            if (ban != null) {
                cloudPlayer.kick(ban.getBanMessage(cloudPlayer.getLocale(), cloudPlayer.getTimeZone()));
                return;
            }

            if (cloud.getCloudConfig().getResourcePackUrl() != null) {
                cloudPlayer.setResourcePack(cloud.getCloudConfig().getResourcePackUrl(),
                        cloud.getCloudConfig().getResourcePackHash(), cloud.getCloudConfig().getResourcePackRequired(),
                        cloud.getCloudConfig().getResourcePackPrompt());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProfileWhitelistVerify(ProfileWhitelistVerifyEvent event) {
        if (event.getPlayerProfile().getId() == null) {
            return;
        }

        try {
            Cloud.getInstance().getPersistence().runInTransaction(transaction -> {
                transaction.suppressCommit();

                net.chrotos.chrotoscloud.player.Player player = cloud.getPlayerManager().getOrCreatePlayer(event.getPlayerProfile().getId(),
                        event.getPlayerProfile().getName());

                event.setWhitelisted(event.isWhitelisted() || event.isOp() || player.hasPermission("minecraft.command.op")
                        || player.hasPermission("cloud.server.join." + cloud.getGameMode())); // TODO remove op?
            });
        } catch (PlayerSoftDeletedException e) {
            event.setWhitelisted(false);
            event.kickMessage(Component.translatable("cloud.player.deleted", NamedTextColor.RED));
        } catch (Exception e) {
            e.printStackTrace();
            event.setWhitelisted(false);
            event.kickMessage(Component.translatable("cloud.error", NamedTextColor.RED));
            cloud.getPlayerManager().logoutPlayer(event.getPlayerProfile().getId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onPlayerLeave(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        event.renderer(renderer);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerResourcePack(PlayerResourcePackStatusEvent event) {
        net.chrotos.chrotoscloud.player.Player player = cloud.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player.getSidedPlayer() == null) {
            return;
        }

        ((PaperSidedPlayer)player.getSidedPlayer()).onResourcePackStatus(event);
    }

    protected void onPlayerLeave(@NonNull Player player) {
        AtomicReference<net.chrotos.chrotoscloud.player.Player> cloudPlayer = new AtomicReference<>();

        cloud.getPersistence().runInTransaction(databaseTransaction -> {
            try {
                cloudPlayer.set(cloud.getPlayerManager().getPlayer(player.getUniqueId()));
                if (cloudPlayer.get() == null) {
                    return;
                }

                if (cloud.isInventorySavingEnabled()) {
                    saveInventory(cloudPlayer.get(), player);
                }

                saveScoreboardTags(cloudPlayer.get(), player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (cloudPlayer.get() != null) {
            cloud.getPlayerManager().logoutPlayer(cloudPlayer.get());
        }
    }

    private void saveScoreboardTags(@NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer, @NonNull Player player) {
        GameState state = cloudPlayer.getStates(cloud.getGameMode()).stream()
                .filter(gameState -> gameState != null && gameState.getName().equals("cloud:tags"))
                .findFirst().orElse(null);

        JsonArray jsonArray = new JsonArray();
        player.getScoreboardTags().forEach(jsonArray::add);
        String json = gson.toJson(jsonArray);

        if (state == null) {
            GameState gameState = new CloudGameState(UUID.randomUUID(), "cloud:tags", cloud.getGameMode(),
                    cloudPlayer, json);
            cloudPlayer.getStates().add(gameState);
        } else {
            state.setState(json);
        }

        cloud.getPersistence().save(cloudPlayer);
    }

    private void loadScoreboardTags(@NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer, @NonNull Player player) {
        GameState state = cloudPlayer.getStates(cloud.getGameMode()).stream()
                .filter(gameState -> gameState != null && gameState.getName().equals("cloud:tags"))
                .findFirst().orElse(null);

        if (state != null) {
            JsonArray jsonArray = gson.fromJson(state.getState(), JsonArray.class);

            jsonArray.forEach(jsonElement -> player.addScoreboardTag(jsonElement.getAsString()));
        }
    }

    private void loadInventory(@NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer, @NonNull Player player) throws InvalidConfigurationException {
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
        PlayerInventory inventory = cloudPlayer.getInventory(cloud.getGameMode());

        YamlConfiguration inventoryContent = new YamlConfiguration();
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            inventoryContent.set(String.valueOf(i), player.getInventory().getItem(i));
        }

        if (inventory == null) {
            inventory = new CloudPlayerInventory(UUID.randomUUID(), cloud.getGameMode(), cloudPlayer, inventoryContent.saveToString());
            cloudPlayer.getInventories().add(inventory);
        } else {
            inventory.setContent(inventoryContent.saveToString());
        }

        Cloud.getInstance().getPersistence().save(cloudPlayer);
    }

    private Ban getBan(@NonNull net.chrotos.chrotoscloud.player.Player cloudPlayer) {
        AtomicReference<Ban> ban = new AtomicReference<>();

        Cloud.getInstance().getPersistence().runInTransaction(transaction -> {
            transaction.suppressCommit();

            ban.set(cloudPlayer.getActiveBan());
        });

        return ban.get();
    }
}

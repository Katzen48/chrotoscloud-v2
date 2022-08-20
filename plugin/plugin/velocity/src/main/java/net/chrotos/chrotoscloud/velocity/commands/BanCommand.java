package net.chrotos.chrotoscloud.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import lombok.NonNull;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.player.Ban;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import net.chrotos.chrotoscloud.velocity.CloudPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class BanCommand {
    public static void register(@NonNull final CloudPlugin plugin) {
        plugin.getProxyServer().getCommandManager().register(new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("ban")
                .requires(source -> source.hasPermission("velocity.command.ban"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("players", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            plugin.getSynchronizer().getPlayers().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("reason", StringArgumentType.word())
                            .executes(context -> {
                                String playerName = context.getArgument("player", String.class);
                                String reason = context.getArgument("reason", String.class);

                                Player player = Cloud.getInstance().getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder()
                                        .columnFilters(Collections.singletonMap("name", playerName)).build());

                                if (player == null) {
                                    context.getSource().sendMessage(Component.text("Player ", NamedTextColor.RED)
                                            .append(Component.text(playerName, NamedTextColor.GOLD))
                                            .append(Component.text(" does not exist!", NamedTextColor.RED)));

                                    return Command.SINGLE_SUCCESS;
                                }

                                ban(player, plugin.getProxyServer().getPlayer(player.getUniqueId()).orElse(null),
                                        reason, 0);

                                return Command.SINGLE_SUCCESS;
                            })
                            .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("days", IntegerArgumentType.integer(1))
                                    .executes(context -> {
                                        String playerName = context.getArgument("player", String.class);
                                        String reason = context.getArgument("reason", String.class);
                                        int days = context.getArgument("days", Integer.class);

                                        Player player = Cloud.getInstance().getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder()
                                                .columnFilters(Collections.singletonMap("name", playerName)).build());

                                        if (player == null) {
                                            context.getSource().sendMessage(Component.text("Player ", NamedTextColor.RED)
                                                    .append(Component.text(playerName, NamedTextColor.GOLD))
                                                    .append(Component.text(" does not exist!", NamedTextColor.RED)));

                                            return Command.SINGLE_SUCCESS;
                                        }

                                            ban(player, plugin.getProxyServer().getPlayer(player.getUniqueId()).orElse(null),
                                                    reason, days);

                                            return Command.SINGLE_SUCCESS;
                                    })
                            )
                        )
                ).build()));
    }

    private static void ban(@NonNull Player cloudPlayer, com.velocitypowered.api.proxy.Player player, @NonNull String reason, int days) {
        Calendar expiresAt = null;
        if (days > 0) {
            expiresAt = Calendar.getInstance();
            expiresAt.add(Calendar.DAY_OF_MONTH, days);
        }

        Ban ban = cloudPlayer.ban(reason, expiresAt);
        if (cloudPlayer.getSidedPlayer() == null && player != null) {
            Locale locale = player.getEffectiveLocale();
            if (locale == null) {
                locale = Locale.US;
            }

            player.disconnect(ban.getBanMessage(locale));
        }
    }
}

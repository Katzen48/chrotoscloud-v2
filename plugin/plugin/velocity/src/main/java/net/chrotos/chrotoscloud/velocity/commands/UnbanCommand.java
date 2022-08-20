package net.chrotos.chrotoscloud.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import lombok.NonNull;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import net.chrotos.chrotoscloud.velocity.CloudPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;

public class UnbanCommand {
    public static void register(@NonNull final CloudPlugin plugin) {
        plugin.getProxyServer().getCommandManager().register(new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("ban")
            .requires(source -> source.hasPermission("velocity.command.unban"))
            .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.word())
                .executes(context -> {
                    String playerName = context.getArgument("player", String.class);

                    Player player = Cloud.getInstance().getPersistence().getOne(CloudPlayer.class, DataSelectFilter.builder()
                            .columnFilters(Collections.singletonMap("name", playerName)).build());

                    if (player == null) {
                        context.getSource().sendMessage(Component.text("Player ", NamedTextColor.RED)
                                .append(Component.text(playerName, NamedTextColor.GOLD))
                                .append(Component.text(" does not exist!", NamedTextColor.RED)));

                        return Command.SINGLE_SUCCESS;
                    }

                    if (!player.isBanned()) {
                        context.getSource().sendMessage(Component.text("Player ", NamedTextColor.RED)
                                .append(Component.text(player.getName(), NamedTextColor.GOLD))
                                .append(Component.text(" is not banned!")));

                        return Command.SINGLE_SUCCESS;
                    }
                    player.unban();

                    context.getSource().sendMessage(Component.text("Player ", NamedTextColor.RED)
                            .append(Component.text(player.getName(), NamedTextColor.GOLD))
                            .append(Component.text(" has been unbanned!")));

                    return Command.SINGLE_SUCCESS;
                }))));
    }
}

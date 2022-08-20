package net.chrotos.chrotoscloud.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class HubCommand implements SimpleCommand {
    private final ProxyServer proxyServer;

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("This command can only be executed by players!"));
            return;
        }

        Optional<ServerConnection> server = player.getCurrentServer();

        if (server.isEmpty()) {
            player.sendMessage(Component.text("You are not currently connected to any server!"));

            return;
        }

        if (proxyServer.getConfiguration().getAttemptConnectionOrder().contains(server.get().getServerInfo().getName())) {
            player.sendMessage(Component.text("You are already connected to the lobby!")); // TODO: i18n

            return;
        }

        HubCandidate candidate = proxyServer.getConfiguration().getAttemptConnectionOrder().stream().map(serverName -> {
            RegisteredServer lobbyServer = proxyServer.getServer(serverName).orElse(null);

            if (lobbyServer == null) {
                return null;
            }

            ServerPing.Players players = lobbyServer.ping().join().getPlayers().orElse(null);

            if (players == null) {
                return null;
            }

            if ((players.getMax() - 2) >= players.getOnline()) {
                return new HubCandidate(lobbyServer, players.getOnline());
            }

            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);

        if (candidate == null) {
            return;
        }

        player.createConnectionRequest(candidate.getServer()).fireAndForget();
    }

    @RequiredArgsConstructor
    @Getter
    private static class HubCandidate {
        @NonNull
        private final RegisteredServer server;
        private final int playerCount;
    }
}

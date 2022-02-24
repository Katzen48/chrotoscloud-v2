package net.chrotos.chrotoscloud.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.Optional;

@AllArgsConstructor
public class HubCommand implements SimpleCommand {
    private ProxyServer proxyServer;

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

        int randomIndex = (int) Math.floor(Math.random() *
                                        ((double) proxyServer.getConfiguration().getAttemptConnectionOrder().size()));
        String requestedServer;
        if ((requestedServer = proxyServer.getConfiguration().getAttemptConnectionOrder().get(randomIndex)) == null) {
            return;
        }

        Optional<RegisteredServer> serverOptional;
        if ((serverOptional = proxyServer.getServer(requestedServer)).isEmpty()) {
            return;
        }

        player.createConnectionRequest(serverOptional.get());
    }
}

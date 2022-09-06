package net.chrotos.chrotoscloud.paper.games;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.games.*;
import net.chrotos.chrotoscloud.games.events.*;
import net.chrotos.chrotoscloud.games.gamemode.CloudGameModeManager;
import net.chrotos.chrotoscloud.messaging.queue.Listener;
import net.chrotos.chrotoscloud.messaging.queue.Message;
import net.chrotos.chrotoscloud.messaging.queue.Registration;
import net.chrotos.chrotoscloud.paper.PaperCloud;
import net.chrotos.chrotoscloud.paper.games.events.CloudPlayerConnectEvent;
import net.chrotos.chrotoscloud.paper.games.queue.PaperLeastPlayersQueueManager;
import net.chrotos.chrotoscloud.paper.games.queue.PaperMostPlayersQueueManager;
import net.chrotos.chrotoscloud.paper.games.queue.PaperRandomQueueManager;
import net.chrotos.chrotoscloud.player.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PaperGameManager implements GameManager, AutoCloseable {
    private final PaperCloud cloud;
    @Getter
    private final CloudGameModeManager gameModeManager = new CloudGameModeManager();
    private Registration<GameServerConnectedEvent, Void> connectedEventRegistration;
    private Registration<PlayerKickedEvent, Void> kickedEventRegistration;

    @Override
    public CompletableFuture<GameServer> getGameServer(@NonNull String name) {
        CompletableFuture<GameServer> future = new CompletableFuture<>();

        Registration<Void, GameServerPingResponse> reg = null;
        try {
            reg = cloud.getQueue().register(pingListener(gameServer ->
                    future.complete(gameServer.getGameServer())), "games.server.ping");
            reg.publish(new GameServerPingRequest(name));

            Registration<Void, GameServerPingResponse> finalReg = reg;

            Bukkit.getScheduler().runTaskLater(cloud.getPlugin(), () -> {
                if (finalReg.isConnected()) {
                    try {
                        finalReg.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    future.completeExceptionally(new TimeoutException());
                }
            }, 100L);

            return future.whenComplete((gameServers, throwable) -> {
                try {
                    finalReg.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            future.completeExceptionally(e);

            if (reg != null) {
                try {
                    reg.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
            }
        }

        return future;
    }

    @Override
    public CompletableFuture<List<? extends GameServer>> getGameServers() {
        CompletableFuture<List<? extends GameServer>> future = new CompletableFuture<>();

        Registration<Void, GameServerLookupResponse> reg = null;
        try {
            reg = cloud.getQueue().register(lookupListener(gameServer -> {
                ArrayList<? extends GameServer> gameServers = new ArrayList<>(gameServer.getGameServers());
                future.complete(gameServers);
            }), "games.server.lookup");

            reg.publish(new GameServerLookupRequest());

            Registration<Void, GameServerLookupResponse> finalReg = reg;

            Bukkit.getScheduler().runTaskLater(cloud.getPlugin(), () -> {
                if (finalReg.isConnected()) {
                    try {
                        finalReg.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    future.completeExceptionally(new TimeoutException());
                }
            }, 100L);

            return future.whenComplete((gameServers, throwable) -> {
                try {
                    finalReg.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            future.completeExceptionally(e);

            if (reg != null) {
                try {
                    reg.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
            }
        }

        return future;
    }

    @Override
    public CompletableFuture<List<? extends GameServer>> getGameServers(@NonNull String gameMode) {
        CompletableFuture<List<? extends GameServer>> future = new CompletableFuture<>();

        Registration<Void, GameServerLookupResponse> reg = null;
        try {
            reg = cloud.getQueue().register(lookupListener(gameServer -> {
                ArrayList<? extends GameServer> gameServers = new ArrayList<>(gameServer.getGameServers());
                future.complete(gameServers);
            }), "games.server.lookup");

            reg.publish(new GameServerLookupRequest(gameMode));

            Registration<Void, GameServerLookupResponse> finalReg = reg;

            Bukkit.getScheduler().runTaskLater(cloud.getPlugin(), () -> {
                if (finalReg.isConnected()) {
                    try {
                        finalReg.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    future.completeExceptionally(new TimeoutException());
                }
            }, 100L);

            return future.whenComplete((gameServers, throwable) -> {
                try {
                    finalReg.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            if (reg != null) {
                try {
                    reg.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
            }

            future.completeExceptionally(e);
        }

        return future;
    }


    @Override
    public CompletableFuture<GameServer> getRandom(@NonNull String gameMode) {
        return getGameServers(gameMode).thenApply(gameServers -> {
            List<GameServer> servers = gameServers.stream()
                    .filter(server -> server.getMaxPlayers() == 0 || server.getPlayerCount() < server.getMaxPlayers() - 2)
                    .collect(Collectors.toList());

            if (servers.size() > 0) {
                return servers.get((int) (Math.random() * servers.size()));
            }

            return null;
        });
    }

    @Override
    public void requestTeleport(@NonNull GameServer server, @NonNull Player player) {
        cloud.getQueue().publish("player.teleport.server",
                new PlayerTeleportToServerRequest(player.getUniqueId(), server.getName()));
    }

    public void initialize() throws IOException {
        connectedEventRegistration = cloud.getQueue().register(connectedListener(event ->
                Bukkit.getScheduler().runTask(cloud.getPlugin(), () ->
                        Bukkit.getPluginManager().callEvent(new CloudPlayerConnectEvent(
                            event.getFrom(), cloud.getPlayerManager().getPlayer(event.getPlayerId()))))),
            "games.server.connect:" + cloud.getHostname());

        kickedEventRegistration = cloud.getQueue().register(kickedListener(event -> {
            org.bukkit.entity.Player player = Bukkit.getPlayer(event.getPlayerId());
            if (player == null) {
                return;
            }

            String reason = event.getReason();

            if (reason == null) {
                player.kick();
            } else {
                Component message = LegacyComponentSerializer.builder().build().deserialize(reason);
                player.kick(message);
            }
        }), "games.server.kick");
    }

    @Override
    public QueueManager getQueueManager(@NonNull QueueMode queueMode, @NonNull String gameMode) {
        return switch (queueMode) {
            case RANDOM -> new PaperRandomQueueManager(gameMode, this);
            case LEAST_PLAYERS -> new PaperLeastPlayersQueueManager(gameMode, this);
            case MOST_PLAYERS -> new PaperMostPlayersQueueManager(gameMode, this);
            default -> throw new IllegalArgumentException("Not implemented");
        };
    }

    private Listener<Void, GameServerLookupResponse> lookupListener(Consumer<GameServerLookupResponse> callback) {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<Void> object, @NonNull String sender) {
            }

            @Override
            public void onReply(@NonNull Message<GameServerLookupResponse> object, @NonNull String sender) {
                callback.accept(object.getMessage());
            }

            @Override
            public Class<GameServerLookupResponse> getReplyClass() {
                return GameServerLookupResponse.class;
            }

            @Override
            public @NonNull Class<Void> getMessageClass() {
                return Void.class;
            }
        };
    }

    private Listener<Void, GameServerPingResponse> pingListener(Consumer<GameServerPingResponse> callback) {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<Void> object, @NonNull String sender) {
            }

            @Override
            public void onReply(@NonNull Message<GameServerPingResponse> object, @NonNull String sender) {
                callback.accept(object.getMessage());
            }

            @Override
            public Class<GameServerPingResponse> getReplyClass() {
                return GameServerPingResponse.class;
            }

            @Override
            public @NonNull Class<Void> getMessageClass() {
                return Void.class;
            }
        };
    }

    private Listener<PlayerKickedEvent, Void> kickedListener(Consumer<PlayerKickedEvent> callback) {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<PlayerKickedEvent> object, @NonNull String sender) {
                callback.accept(object.getMessage());
            }

            @Override
            public void onReply(@NonNull Message<Void> object, @NonNull String sender) {}

            @Override
            public Class<Void> getReplyClass() {
                return Void.class;
            }

            @Override
            public @NonNull Class<PlayerKickedEvent> getMessageClass() {
                return PlayerKickedEvent.class;
            }
        };
    }

    private Listener<GameServerConnectedEvent, Void> connectedListener(Consumer<GameServerConnectedEvent> callback) {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<GameServerConnectedEvent> object, @NonNull String sender) {
                callback.accept(object.getMessage());
            }

            @Override
            public void onReply(@NonNull Message<Void> object, @NonNull String sender) {}

            @Override
            public Class<Void> getReplyClass() {
                return Void.class;
            }

            @Override
            public @NonNull Class<GameServerConnectedEvent> getMessageClass() {
                return GameServerConnectedEvent.class;
            }
        };
    }

    @Override
    public void close() throws Exception {
        if (connectedEventRegistration != null && connectedEventRegistration.isSubscribed()) {
            connectedEventRegistration.close();
        }

        if (kickedEventRegistration != null && kickedEventRegistration.isSubscribed()) {
            kickedEventRegistration.close();
        }
    }
}
